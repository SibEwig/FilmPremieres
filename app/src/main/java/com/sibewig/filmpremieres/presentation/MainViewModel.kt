package com.sibewig.filmpremieres.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibewig.filmpremieres.domain.GetMovieListFlowUseCase
import com.sibewig.filmpremieres.domain.LoadDataUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getMovieListFlowUseCase: GetMovieListFlowUseCase,
    private val loadDataUseCase: LoadDataUseCase
) : ViewModel() {

    val movieListFlow = getMovieListFlowUseCase()
    private var isLoading = false

    fun loadData() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            loadDataUseCase()
            isLoading = false
        }
    }

    init {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }
}
