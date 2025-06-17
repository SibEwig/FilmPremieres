package com.sibewig.filmpremieres.data.repository

import android.util.Log
import com.sibewig.filmpremieres.data.database.FavouriteDao
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
    private val apiService: ApiService,
    private val favouriteDao: FavouriteDao
) : MovieRepository {

    private val movieList = mutableListOf<Movie>()
    private val searchResultList = mutableListOf<Movie>()

    private val _movieListFlow = MutableSharedFlow<List<Movie>>(replay = 1)
    override val movieListFlow: SharedFlow<List<Movie>> = _movieListFlow

    private val _movieInfoFlow = MutableStateFlow<Movie?>(null)
    override val movieInfoFlow: StateFlow<Movie?>
        get() = _movieInfoFlow.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Unit>()
    override val errorFlow: SharedFlow<Unit>
        get() = _errorFlow.asSharedFlow()

    private val _fullListLoaded = MutableStateFlow(false)
    override val fullListLoaded: StateFlow<Boolean>
        get() = _fullListLoaded.asStateFlow()

    private var page = INITIAL_PAGE
    private var totalMovies = INITIAL_SIZE
    private var totalSearchResult = INITIAL_SIZE
    private val premiereDateRange: String = generatePremiereRange()

    override suspend fun loadMovieList() {
        if (totalMovies == 0 || movieList.size < totalMovies) {
            try {
                val response = apiService.loadMovies(page = page, premiereRange = premiereDateRange)
                val dtoList = response.movies
                totalMovies = response.total
                Log.d(TAG, "Total movie list size: $totalMovies")
                val movies = dtoList.map { mapper.mapMovieDtoToDomain(it) }
                if (movies.isNotEmpty()) {
                    movieList.addAll(movies)
                    _movieListFlow.emit(movieList.toList())
                    page++
                }
            } catch (e: Exception) {
                _errorFlow.emit(Unit)
                Log.e(TAG, "Error loading movie list", e)
            }
        } else {
            _fullListLoaded.value = true
            _movieListFlow.emit(movieList.toList())
        }
    }

    override suspend fun searchMovie(query: String) {
        if (totalSearchResult == 0 || searchResultList.size < totalSearchResult) {
            try {
                val response = apiService.searchMovie(query)
                val dtoList = response.movies
                totalSearchResult = response.total
                Log.d(TAG, "Total movie list size: $totalMovies")
                Log.d(TAG, "Loaded: ${dtoList.joinToString()}")
                val movies = dtoList
                    .map { mapper.mapMovieDtoToDomain(it) }
                    .filter { it.name.contains(query, ignoreCase = true) }
                if (movies.isNotEmpty()) {
                    searchResultList.clear()
                    _movieListFlow.emit(searchResultList.toList())
                    searchResultList.addAll(movies)
                    _movieListFlow.emit(searchResultList.toList())
                }
                if (totalSearchResult == movies.size) _fullListLoaded.value = true
            } catch (e: Exception) {
                _errorFlow.emit(Unit)
                Log.e(TAG, "Error while searching", e)
            }
        } else {
            _fullListLoaded.value = true
            _movieListFlow.emit(searchResultList.toList())
        }

    }

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

    override suspend fun getFavouriteList() {
        _movieListFlow.emit(
            favouriteDao.getAllFavorites().map { mapper.mapFavouriteEntityToMovie(it) }
        )
    }

    override suspend fun addToFavorites(movie: Movie) {
        favouriteDao.addToFavorites(mapper.mapMovieToFavourite(movie))
    }

    override suspend fun removeFromFavorites(movie: Movie) {
        favouriteDao.removeFromFavorites(mapper.mapMovieToFavourite(movie))
    }

    override suspend fun isFavourite(movieId: Int) = favouriteDao.isFavorite(movieId)

    private fun generatePremiereRange(): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1)
        val endDate = startDate.plusMonths(12).minusDays(1)
        return "${startDate.format(formatter)}-${endDate.format(formatter)}".also {
            Log.d(TAG, "Premiere range: $it")
        }
    }

    companion object {

        private const val INITIAL_PAGE = 1
        private const val INITIAL_SIZE = 0
        private const val TAG = "RepositoryImpl"
    }
}