package tv.own.owntv.trakt

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

// --- Modèles de données ---

data class DeviceCodeResponse(
    @SerializedName("device_code") val deviceCode: String,
    @SerializedName("user_code") val userCode: String,
    @SerializedName("verification_url") val verificationUrl: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("interval") val interval: Int
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("token_type") val tokenType: String
)

data class TraktIds(
    val trakt: Long? = null,
    val imdb: String? = null,
    val tmdb: Long? = null,
    val tvdb: Long? = null
)

data class TraktEpisode(
    val number: Int
)

data class TraktSeason(
    val number: Int,
    val episodes: List<TraktEpisode>
)

data class TraktShowPayload(
    val ids: TraktIds,
    val seasons: List<TraktSeason>
)

data class TraktMoviePayload(
    val ids: TraktIds
)

data class SyncHistoryRequest(
    val movies: List<TraktMoviePayload>? = null,
    val shows: List<TraktShowPayload>? = null
)

// --- Interface Retrofit ---

interface TraktApiService {

    @FormUrlEncoded
    @POST("oauth/device/code")
    suspend fun getDeviceCode(
        @Field("client_id") clientId: String = TraktConfig.CLIENT_ID
    ): Response<DeviceCodeResponse>

    @FormUrlEncoded
    @POST("oauth/device/token")
    suspend fun getDeviceToken(
        @Field("code") deviceCode: String,
        @Field("client_id") clientId: String = TraktConfig.CLIENT_ID
    ): Response<TokenResponse>

    @POST(TraktConfig.ENDPOINT_SYNC_HISTORY)
    suspend fun addToHistory(
        @Header(TraktConfig.HEADER_AUTHORIZATION) authorization: String,
        @Header(TraktConfig.HEADER_API_VERSION) apiVersion: String = TraktConfig.API_VERSION,
        @Header(TraktConfig.HEADER_API_KEY) apiKey: String = TraktConfig.CLIENT_ID,
        @Body request: SyncHistoryRequest
    ): Response<Any>

    @POST(TraktConfig.ENDPOINT_SYNC_HISTORY_REMOVE)
    suspend fun removeFromHistory(
        @Header(TraktConfig.HEADER_AUTHORIZATION) authorization: String,
        @Header(TraktConfig.HEADER_API_VERSION) apiVersion: String = TraktConfig.API_VERSION,
        @Header(TraktConfig.HEADER_API_KEY) apiKey: String = TraktConfig.CLIENT_ID,
        @Body request: SyncHistoryRequest
    ): Response<Any>
}