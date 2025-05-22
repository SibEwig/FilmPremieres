package com.sibewig.filmpremieres.domain

import javax.inject.Inject

class GetMovieListFlowUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    operator fun invoke() = repository.movieListFlow
}