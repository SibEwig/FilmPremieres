package com.sibewig.filmpremieres.domain

import kotlinx.coroutines.flow.StateFlow

interface MovieRepository {

    val movieListFlow: StateFlow<List<Movie>>

    suspend fun getMovieInfo(id: Int): Movie

    suspend fun loadMovieList()
}