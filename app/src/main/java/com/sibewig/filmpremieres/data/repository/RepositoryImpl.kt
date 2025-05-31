package com.sibewig.filmpremieres.data.repository

import android.util.Log
import com.sibewig.filmpremieres.data.mapper.MovieMapper
import com.sibewig.filmpremieres.data.network.ApiService
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val mapper: MovieMapper,
    private val apiService: ApiService
) : MovieRepository {

    private val movieList = mutableListOf<Movie>()

    private val _movieListFlow = MutableStateFlow<List<Movie>>(emptyList())
    override val movieListFlow: StateFlow<List<Movie>>
        get() = _movieListFlow.asStateFlow()

    private val _movieInfoFlow = MutableStateFlow<Movie?>(null)
    override val movieInfoFlow: StateFlow<Movie?>
        get() = _movieInfoFlow.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Unit>()
    override val errorFlow: SharedFlow<Unit>
        get() = _errorFlow.asSharedFlow()

    private var page = INITIAL_PAGE

    override suspend fun loadMovieInfo(id: Int) {
        try {
            _movieInfoFlow.value = null
            apiService.loadMovie(id).apply {
                mapper.mapMovieDtoToDomain(this).also {
                    Log.d(TAG, "Loaded in repo: $it")
                    _movieInfoFlow.emit(it)
                }
            }
        } catch (e: Exception) {
            _errorFlow.emit(Unit)
            Log.e(TAG, "Error loading movie list", e)
        }
    }

    private fun generatePremiereRange(): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val startDate = LocalDate.now().withDayOfMonth(1)
        val endDate = startDate.plusMonths(12).minusDays(1)
        return "${startDate.format(formatter)}-${endDate.format(formatter)}".also {
            Log.d(TAG, "Premiere range: $it")
        }
    }

    override suspend fun loadMovieList() {
        val premiereRange = generatePremiereRange()
        try {
            val dtoList = apiService.loadMovies(page = page, premiereRange = premiereRange).movies
            val movies = dtoList.map { mapper.mapMovieDtoToDomain(it) }
            if (movies.isNotEmpty()) {
                movieList.addAll(movies)
                _movieListFlow.value = movieList.toList()
                page++
            }
        } catch (e: Exception) {
            _errorFlow.emit(Unit)
            Log.e(TAG, "Error loading movie list", e)
        }
    }

    companion object {

        private const val INITIAL_PAGE = 1
        private const val TAG = "RepositoryImpl"
    }
}