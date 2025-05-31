package com.sibewig.filmpremieres.domain

sealed class MovieDetailActivityState {
    data object Loading: MovieDetailActivityState()
    data class Content(val content: Movie): MovieDetailActivityState()
    data class Error(val error: String): MovieDetailActivityState()
}