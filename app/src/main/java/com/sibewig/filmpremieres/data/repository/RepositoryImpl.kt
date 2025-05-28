package com.sibewig.filmpremieres.data.repository

import android.util.Log
import com.sibewig.filmpremieres.data.mapper.MovieMapper
import com.sibewig.filmpremieres.data.network.ApiService
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val mapper: MovieMapper,
    private val apiService: ApiService
) : MovieRepository {

    private val movieList = mutableListOf<Movie>()

    private val _movieListFlow = MutableStateFlow<List<Movie>>(emptyList())
    override val movieListFlow: StateFlow<List<Movie>>
        get() = _movieListFlow.asStateFlow()

    private var page = INITIAL_PAGE

    override suspend fun getMovieInfo(id: Int): Movie {
        return mapper.mapMovieDtoToDomain(apiService.loadMovie(id))
    }

    override suspend fun loadMovieList() {
        try {
            val dtoList = apiService.loadMovies(page).movies
            for (movie in dtoList) {
                Log.d(TAG, "Trailers (id: ${movie.id}): ${movie.trailerList?.toString()}")
            }
            val movies = dtoList.map { mapper.mapMovieDtoToDomain(it) }
            if (movies.isNotEmpty()) {
                movieList.addAll(movies)
                _movieListFlow.value = movieList.toList()

                page++
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading movie list", e)
        }
    }

    companion object {

        private const val INITIAL_PAGE = 1
        private const val TAG = "RepositoryImpl"
    }
}