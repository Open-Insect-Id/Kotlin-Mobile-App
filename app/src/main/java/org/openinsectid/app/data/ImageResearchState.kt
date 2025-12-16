package org.openinsectid.app.data

sealed class ImageSearchState {
    data object Idle : ImageSearchState()
    data object Loading : ImageSearchState()
    data class Success(val images: List<FetchedImage>) : ImageSearchState()
    data class Error(val error: ImageSearchError) : ImageSearchState()
}
