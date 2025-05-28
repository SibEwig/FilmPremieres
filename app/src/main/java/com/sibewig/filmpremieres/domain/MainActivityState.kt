package com.sibewig.filmpremieres.domain

sealed class MainActivityState {
    data object Loading: MainActivityState()
    data class Content(val content: List<MovieListItem>): MainActivityState()
}