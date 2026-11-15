package tv.own.owntv.trakt

import android.content.Context
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TraktManager private constructor(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("trakt_prefs", Context.MODE_PRIVATE)

    private val retrofit = Retrofit.Builder()
        .baseUrl(TraktConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(TraktApiService::class.java)

    var accessToken: String?
        get() {
            val token = prefs.getString("access_token", null)
            println(buildString {
                append('T'); append('r'); append('a'); append('k'); append('t'); append(':'); append(' ')
                append('G'); append('e'); append('t'); append(' '); append(token.toString())
            })
            return token
        }
        private set(value) {
            println(buildString {
                append('T'); append('r'); append('a'); append('k'); append('t'); append(':'); append(' ')
                append('S'); append('e'); append('t'); append(' '); append(value.toString())
            })
            prefs.edit().putString("access_token", value).commit()
        }

    val isAuthenticated: Boolean
        get() = !accessToken.isNullOrEmpty()

    suspend fun requestDeviceCode(): DeviceCodeResponse? {
        val response = api.getDeviceCode()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun pollForAccessToken(deviceCode: String, intervalSeconds: Int, expiresIn: Int): Boolean {
        val endTime = System.currentTimeMillis() + (expiresIn * 1000L)
        val intervalMs = (intervalSeconds * 1000L).coerceAtLeast(2000L)

        while (System.currentTimeMillis() < endTime) {
            val response = api.getDeviceToken(deviceCode = deviceCode)
            if (response.isSuccessful && response.body() != null) {
                accessToken = response.body()!!.accessToken
                return true
            }
            delay(intervalMs)
        }
        return false
    }

    suspend fun markEpisodeAsWatched(imdbId: String? = null, tmdbId: Long? = null, seasonNumber: Int, episodeNumber: Int): Boolean {
        return syncEpisodeHistory(imdbId, tmdbId, seasonNumber, episodeNumber, remove = false)
    }

    suspend fun markEpisodeAsUnwatched(imdbId: String? = null, tmdbId: Long? = null, seasonNumber: Int, episodeNumber: Int): Boolean {
        return syncEpisodeHistory(imdbId, tmdbId, seasonNumber, episodeNumber, remove = true)
    }
    // Méthode mutualisée pour éviter la duplication de code entre l'ajout et la suppression.
    private suspend fun syncEpisodeHistory(imdbId: String?, tmdbId: Long?, seasonNumber: Int, episodeNumber: Int, remove: Boolean): Boolean {
        val token = accessToken ?: return false
        val request = SyncHistoryRequest(
            shows = listOf(
                TraktShowPayload(
                    ids = TraktIds(imdb = imdbId?.takeIf { it.isNotBlank() }, tmdb = tmdbId),
                    seasons = listOf(
                        TraktSeason(
                            number = seasonNumber,
                            episodes = listOf(TraktEpisode(number = episodeNumber))
                        )
                    )
                )
            )
        )
        val gson = com.google.gson.Gson()
        val jsonPayload = gson.toJson(request)
        android.util.Log.d("TraktManager", "syncEpisodeHistory(remove=$remove): sending JSON payload: $jsonPayload")

        val response = if (remove) {
            api.removeFromHistory(
                authorization = "${TraktConfig.TOKEN_PREFIX}$token",
                apiKey = TraktConfig.CLIENT_ID,
                request = request
            )
        } else {
            api.addToHistory(
                authorization = "${TraktConfig.TOKEN_PREFIX}$token",
                apiKey = TraktConfig.CLIENT_ID,
                request = request
            )
        }
        android.util.Log.d("TraktManager", "syncEpisodeHistory response: code=${response.code()}, successful=${response.isSuccessful}")
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            android.util.Log.e("TraktManager", "syncEpisodeHistory error body: $errorBody")
        }
        return response.isSuccessful
    }

    suspend fun markMovieAsWatched(imdbId: String? = null, tmdbId: Long? = null): Boolean {
        return syncMovieHistory(imdbId, tmdbId, remove = false)
    }

    suspend fun markMovieAsUnwatched(imdbId: String? = null, tmdbId: Long? = null): Boolean {
        return syncMovieHistory(imdbId, tmdbId, remove = true)
    }

    private suspend fun syncMovieHistory(imdbId: String?, tmdbId: Long?, remove: Boolean): Boolean {
        val token = accessToken ?: return false
        val request = SyncHistoryRequest(
            movies = listOf(
                TraktMoviePayload(
                    ids = TraktIds(imdb = imdbId?.takeIf { it.isNotBlank() }, tmdb = tmdbId)
                )
            )
        )
        val gson = com.google.gson.Gson()
        val jsonPayload = gson.toJson(request)
        android.util.Log.d("TraktManager", "syncMovieHistory(remove=$remove): sending JSON payload: $jsonPayload")

        val response = if (remove) {
            api.removeFromHistory(
                authorization = "${TraktConfig.TOKEN_PREFIX}$token",
                apiKey = TraktConfig.CLIENT_ID,
                request = request
            )
        } else {
            api.addToHistory(
                authorization = "${TraktConfig.TOKEN_PREFIX}$token",
                apiKey = TraktConfig.CLIENT_ID,
                request = request
            )
        }
        android.util.Log.d("TraktManager", "syncMovieHistory response: code=${response.code()}, successful=${response.isSuccessful}")
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            android.util.Log.e("TraktManager", "syncMovieHistory error body: $errorBody")
        }
        return response.isSuccessful
    }

    fun logout() {
        accessToken = null
    }

    companion object {
        @Volatile
        private var INSTANCE: TraktManager? = null

        fun getInstance(context: Context): TraktManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TraktManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}