package org.openinsectid.app.data


sealed class ImageSearchError(
    val userMessage: String,
    val debugMessage: String,
    cause: Throwable? = null
) : Throwable(debugMessage, cause) {

    class Network(cause: Throwable) : ImageSearchError(
        userMessage = "No internet connection.",
        debugMessage = "Network error: ${cause.message}",
        cause = cause
    )

    class Api(cause: Throwable) : ImageSearchError(
        userMessage = "Could not load images.",
        debugMessage = "API error: ${cause.message}",
        cause = cause
    )

    class Unknown(cause: Throwable) : ImageSearchError(
        userMessage = "Unknown error while loading images.",
        debugMessage = "Unknown error: ${cause.message}",
        cause = cause
    )
}
