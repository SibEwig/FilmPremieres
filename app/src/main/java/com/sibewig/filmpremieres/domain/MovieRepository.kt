package com.sibewig.filmpremieres.domain

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MovieRepository {

    val movieListFlow: StateFlow<List<Movie>>

    val errorFlow: SharedFlow<Unit>

    val movieInfoFlow: SharedFlow<Movie?>

    suspend fun loadMovieInfo(id: Int)

    suspend fun loadMovieList()
}