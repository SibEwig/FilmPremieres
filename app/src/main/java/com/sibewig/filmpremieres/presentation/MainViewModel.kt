package com.sibewig.filmpremieres.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.*
import com.sibewig.filmpremieres.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getMovieListFlowUseCase: GetMovieListFlowUseCase,
    private val loadDataUseCase: LoadDataUseCase,
    private val getErrorFlowUseCase: GetErrorFlowUseCase,
    private val getFavouriteListUseCase: GetFavouriteListUseCase,
    private val getFullListLoadedUseCase: GetFullListLoadedUseCase,
    private val searchMovieUseCase: SearchMovieUseCase
) : ViewModel() {

    private val _screenModeFlow = MutableStateFlow(ScreenMode.MAIN)
    val screenMode: StateFlow<ScreenMode> = _screenModeFlow

    private val errorFlow = getErrorFlowUseCase()
        .map { MainActivityState.Error(ERROR_LOADING) }

    private val isLoadingFlow = MutableSharedFlow<MainActivityState.Loading>()
    val fullListLoaded = getFullListLoadedUseCase()

    private var isPageLoading = false

    val uiState: Flow<MainActivityState> = combine(
        _screenModeFlow,
        getMovieListFlowUseCase()
    ) { screenMode, movies ->
        val items = when (screenMode) {
            ScreenMode.MAIN, ScreenMode.FAVOURITES -> groupMoviesByMonth(movies)
            else -> movies.map { MovieListItem.MovieItem(it) }
        }
        MainActivityState.Content(items) as MainActivityState
    }
        .onStart { emit(MainActivityState.Loading) }
        .mergeWith(isLoadingFlow)
        .mergeWith(errorFlow)

    init {
        setScreenMode(ScreenMode.MAIN)
    }

    fun setScreenMode(mode: ScreenMode, query: String? = null) {
        Log.d(TAG, "Invoked method: setScreenMode($mode, $query)")

        when (mode) {
            ScreenMode.MAIN -> loadInitialData()
            ScreenMode.SEARCH -> performSearch(query.orEmpty())
            ScreenMode.FAVOURITES -> loadFavourites()
        }
        _screenModeFlow.value = mode
    }

    fun loadNextPage() {
        if (_screenModeFlow.value != ScreenMode.MAIN || isPageLoading) return
        Log.d(TAG, "Invoked method: loadNextPage")
        isPageLoading = true
        viewModelScope.launch {
            isLoadingFlow.emit(MainActivityState.Loading)
            loadDataUseCase()
            isPageLoading = false
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            isLoadingFlow.emit(MainActivityState.Loading)
            searchMovieUseCase(query)
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            isLoadingFlow.emit(MainActivityState.Loading)
            getFavouriteListUseCase()
        }
    }

    private fun groupMoviesByMonth(movies: List<Movie>): List<MovieListItem> {
        return movies
            .sortedBy { it.premiere }
            .groupBy {
                val date = it.premiere
                val month = date.monthValue - 1
                val year = date.year
                "${MONTH_NAMES_RU[month]} $year"
            }
            .flatMap { (month, moviesInMonth) ->
                listOf(MovieListItem.Header(month)) + moviesInMonth.map {
                    MovieListItem.MovieItem(it)
                }
            }
    }

    private fun <T> Flow<T>.mergeWith(another: Flow<T>): Flow<T> {
        return merge(this, another)
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val ERROR_LOADING = "Ошибка загрузки. Повторите попытку."
        private val MONTH_NAMES_RU = arrayOf(
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        )
    }
}
