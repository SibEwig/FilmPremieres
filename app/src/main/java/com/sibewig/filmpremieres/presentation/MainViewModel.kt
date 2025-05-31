package com.sibewig.filmpremieres.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.MainActivityState
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieListItem
import com.sibewig.filmpremieres.domain.usecase.GetErrorFlowUseCase
import com.sibewig.filmpremieres.domain.usecase.GetMovieListFlowUseCase
import com.sibewig.filmpremieres.domain.usecase.LoadDataUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getMovieListFlowUseCase: GetMovieListFlowUseCase,
    private val loadDataUseCase: LoadDataUseCase,
    private val getErrorFlowUseCase: GetErrorFlowUseCase
) : ViewModel() {

    private val loadingFlow = MutableSharedFlow<MainActivityState.Loading>()
    private val errorFlow = getErrorFlowUseCase()
        .map { MainActivityState.Error(ERROR_LOADING) }

    val state: Flow<MainActivityState> = getMovieListFlowUseCase()
        .map { movies -> groupMoviesByMonth(movies) }
        .map { MainActivityState.Content(it) as MainActivityState}
        .onStart { emit(MainActivityState.Loading) }
        .mergeWith(loadingFlow)
        .mergeWith(errorFlow)

    private var isLoading = false

    fun loadData() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            loadingFlow.emit(MainActivityState.Loading)
            loadDataUseCase()
            isLoading = false
        }
    }

    private fun <T> Flow<T>.mergeWith(another: Flow<T>): Flow<T> {
        return merge(this, another)
    }

    private fun groupMoviesByMonth(movies: List<Movie>): List<MovieListItem> {
        return movies
            .sortedBy {
                it.premiere
            }
            .groupBy {
                val date = it.premiere
                val month = date.monthValue - 1
                val year = date.year
                val monthNames = MONTH_NAMES_RU
                "${monthNames[month]} $year"
            }
            .flatMap { (month, monthMovies) ->
                listOf(MovieListItem.Header(month)) + monthMovies.map { MovieListItem.MovieItem(it) }
            }
    }

    init {
        viewModelScope.launch {
            loadingFlow.emit(MainActivityState.Loading)
            loadDataUseCase()
        }
    }

    companion object {

        private const val ERROR_LOADING = "Ошибка загрузки. Повторите попытку."
        private val MONTH_NAMES_RU = arrayOf(
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        )
    }
}
