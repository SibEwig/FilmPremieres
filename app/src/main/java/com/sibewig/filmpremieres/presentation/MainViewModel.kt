package com.sibewig.filmpremieres.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.GetMovieListFlowUseCase
import com.sibewig.filmpremieres.domain.LoadDataUseCase
import com.sibewig.filmpremieres.domain.Movie
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getMovieListFlowUseCase: GetMovieListFlowUseCase,
    private val loadDataUseCase: LoadDataUseCase
) : ViewModel() {

    val movieListFlow = getMovieListFlowUseCase()
        .map { movies -> groupMoviesByMonth(movies) }
    private var isLoading = false

    fun loadData() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            loadDataUseCase()
            isLoading = false
        }
    }

    private fun groupMoviesByMonth(movies: List<Movie>): List<MovieListItem> {
        return movies
            .sortedBy {
                it.premiere
            }
            .groupBy {
                val date = it.premiere
                val month = date.monthValue - 1 // от 0 до 11
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
            loadDataUseCase()
        }
    }
}
