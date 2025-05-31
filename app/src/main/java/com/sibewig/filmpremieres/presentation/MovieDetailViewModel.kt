package com.sibewig.filmpremieres.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.MovieDetailActivityState
import com.sibewig.filmpremieres.domain.usecase.GetErrorFlowUseCase
import com.sibewig.filmpremieres.domain.usecase.GetMovieInfoFlowUseCase
import com.sibewig.filmpremieres.domain.usecase.LoadMovieInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    val loadMovieInfoUseCase: LoadMovieInfoUseCase,
    val getMovieInfoFlowUseCase: GetMovieInfoFlowUseCase,
    val getErrorFlowUseCase: GetErrorFlowUseCase
) : ViewModel() {

    private val stateError = getErrorFlowUseCase()
        .map { MovieDetailActivityState.Error(ERROR_LOADING) }

    val state: Flow<MovieDetailActivityState> = getMovieInfoFlowUseCase()
        .filterNotNull()
        .map {
            Log.d(TAG, "Received in VM: $it")
                MovieDetailActivityState.Content(it) as MovieDetailActivityState
        }
        .onStart { emit(MovieDetailActivityState.Loading) }
        .mergeWith(stateError)

    private fun <T> Flow<T>.mergeWith(another: Flow<T>): Flow<T> {
        return merge(this, another)
    }

    fun loadMovieInfo(id: Int) {
        viewModelScope.launch {
            loadMovieInfoUseCase.invoke(id)
        }
    }

    companion object {

        private const val ERROR_LOADING = "Ошибка загрузки. Повторите попытку."
        private const val TAG = "MovieDetailViewModel"
    }
}