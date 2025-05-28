package com.sibewig.filmpremieres.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.GetMovieListFlowUseCase
import com.sibewig.filmpremieres.domain.LoadDataUseCase
import com.sibewig.filmpremieres.domain.MainActivityState
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getMovieListFlowUseCase: GetMovieListFlowUseCase,
    private val loadDataUseCase: LoadDataUseCase
) : ViewModel() {

    private val loadingFlow = MutableSharedFlow<MainActivityState.Loading>()

    val state: Flow<MainActivityState> = getMovieListFlowUseCase()
        .map { movies -> groupMoviesByMonth(movies) }
        .map { MainActivityState.Content(it) as MainActivityState}
        .onStart { emit(MainActivityState.Loading) }
        .mergeWith(loadingFlow)

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

                val monthNames = arrayOf(
                    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
                )
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
}
