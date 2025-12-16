package org.openinsectid.app.data


sealed interface ImageSearchState {
    object Idle : ImageSearchState
    object Loading : ImageSearchState
    data class Success(val images: List<DdgImage>) : ImageSearchState
    data class Error(val message: String) : ImageSearchState
}
