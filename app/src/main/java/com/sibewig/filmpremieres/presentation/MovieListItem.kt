package com.sibewig.filmpremieres.presentation

import com.sibewig.filmpremieres.domain.Movie

sealed class MovieListItem {
    data class Header(val month: String) : MovieListItem()
    data class MovieItem(val movie: Movie) : MovieListItem()
}