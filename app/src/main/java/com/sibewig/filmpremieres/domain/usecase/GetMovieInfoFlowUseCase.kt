package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class GetMovieInfoFlowUseCase @Inject constructor(
    val repository: MovieRepository
) {

    operator fun invoke() = repository.movieInfoFlow

}