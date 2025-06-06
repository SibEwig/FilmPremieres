package com.sibewig.filmpremieres.domain

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MovieRepository {

    val movieListFlow: SharedFlow<List<Movie>>

    val errorFlow: SharedFlow<Unit>

    val movieInfoFlow: SharedFlow<Movie?>

    val fullListLoaded: StateFlow<Boolean>

    suspend fun loadMovieInfo(id: Int)

    suspend fun loadMovieList()

    suspend fun getFavouriteList()

    suspend fun addToFavorites(movie: Movie)

    suspend fun removeFromFavorites(movie: Movie)

    suspend fun isFavourite(movieId: Int): Boolean

    suspend fun searchMovie(query: String)
}