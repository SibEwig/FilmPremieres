package com.sibewig.filmpremieres.domain

import javax.inject.Inject

class LoadDataUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke() = repository.loadMovieList()
}