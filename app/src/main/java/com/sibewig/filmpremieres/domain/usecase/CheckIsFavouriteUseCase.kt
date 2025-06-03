package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class CheckIsFavouriteUseCase @Inject constructor(
    val repository: MovieRepository
) {

    suspend operator fun invoke(movieId: Int) = repository.isFavourite(movieId)
}