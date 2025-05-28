package com.sibewig.filmpremieres.domain

sealed class MovieListItem {
    data class Header(val month: String) : MovieListItem()
    data class MovieItem(val movie: Movie) : MovieListItem()
}