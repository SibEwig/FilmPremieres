package com.sibewig.filmpremieres.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieDetailActivityState
import com.sibewig.filmpremieres.domain.usecase.AddToFavouritesUseCase
import com.sibewig.filmpremieres.domain.usecase.CheckIsFavouriteUseCase
import com.sibewig.filmpremieres.domain.usecase.GetErrorFlowUseCase
import com.sibewig.filmpremieres.domain.usecase.GetMovieInfoFlowUseCase
import com.sibewig.filmpremieres.domain.usecase.LoadMovieInfoUseCase
import com.sibewig.filmpremieres.domain.usecase.RemoveFromFavouritesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val loadMovieInfoUseCase: LoadMovieInfoUseCase,
    private val getMovieInfoFlowUseCase: GetMovieInfoFlowUseCase,
    private val getErrorFlowUseCase: GetErrorFlowUseCase,
    private val addToFavouritesUseCase: AddToFavouritesUseCase,
    private val removeFromFavouritesUseCase: RemoveFromFavouritesUseCase,
    private val checkIsFavouriteUseCase: CheckIsFavouriteUseCase
) : ViewModel() {

    private val stateError = getErrorFlowUseCase()
        .map { MovieDetailActivityState.Error(ERROR_LOADING) }

    private val _isFavourite = MutableStateFlow<Boolean?>(null)
    val isFavourite = _isFavourite.asStateFlow()

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

    fun toggleFavourite(movie: Movie) {
        viewModelScope.launch {
            val isFav = _isFavourite.value ?: false
            if (isFav) {
                removeFromFavouritesUseCase(movie)
            } else {
                addToFavouritesUseCase(movie)
            }
            _isFavourite.value = !isFav
        }
    }

    fun checkIsFavourite(movieId: Int) {
        viewModelScope.launch {
            _isFavourite.value = checkIsFavouriteUseCase(movieId)
        }
    }

    companion object {

        private const val ERROR_LOADING = "Ошибка загрузки. Повторите попытку."
        private const val TAG = "MovieDetailViewModel"
    }
}