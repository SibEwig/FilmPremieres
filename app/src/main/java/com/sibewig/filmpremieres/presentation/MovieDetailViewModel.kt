package com.sibewig.filmpremieres.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.GetMovieInfoUseCase
import com.sibewig.filmpremieres.domain.Movie
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    val getMovieInfoUseCase: GetMovieInfoUseCase
): ViewModel() {

    val movieInfo = MutableSharedFlow<Movie>()

    fun loadMovieInfo(id: Int) {
        viewModelScope.launch {
            getMovieInfoUseCase.invoke(id).also {
                movieInfo.emit(it)
            }
        }
    }
}