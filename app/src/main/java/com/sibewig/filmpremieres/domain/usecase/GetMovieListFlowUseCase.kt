package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class GetMovieListFlowUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    operator fun invoke() = repository.movieListFlow
}