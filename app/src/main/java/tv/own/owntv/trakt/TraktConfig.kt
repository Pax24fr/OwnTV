package tv.own.owntv.trakt

object TraktConfig {
    const val CLIENT_ID = "9CXCF2ejv5Dt7ECLtV5j2SHw-mvaZjags0A98g7cgu8"
    const val BASE_URL = "https://api.trakt.tv/"
    const val API_VERSION = "2"

    // Constantes techniques pour les endpoints et headers
    const val ENDPOINT_SYNC_HISTORY = "sync/history"
    const val ENDPOINT_SYNC_HISTORY_REMOVE = "sync/history/remove"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_API_VERSION = "trakt-api-version"
    const val HEADER_API_KEY = "trakt-api-key"
    const val TOKEN_PREFIX = "Bearer "
}