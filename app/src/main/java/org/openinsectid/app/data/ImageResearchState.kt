package org.openinsectid.app.data

import android.net.Uri
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed interface ImageSearchState {
    object Idle : ImageSearchState
    object Loading : ImageSearchState
    data class Success(val images: List<FetchedImage>) : ImageSearchState
    data class Error(val error: ImageSearchError) : ImageSearchState
}

sealed class ImageSearchError(
    val userMessage: String,
    val debugMessage: String,
    cause: Throwable? = null
) : Exception(debugMessage, cause) {

    class Network(t: Throwable) : ImageSearchError(
        userMessage = "Network error",
        debugMessage = t.message ?: "Network failure",
        cause = t
    )

    class Http(code: Int, body: String?) : ImageSearchError(
        userMessage = "Server error",
        debugMessage = "HTTP $code – ${body?.take(500)}"
    )

    class Parsing(t: Throwable) : ImageSearchError(
        userMessage = "Invalid server response",
        debugMessage = t.message ?: "Parsing error",
        cause = t
    )

    class Unknown(t: Throwable) : ImageSearchError(
        userMessage = "Unknown error",
        debugMessage = t.message ?: "Unknown failure",
        cause = t
    )
}
