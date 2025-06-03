package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class GetFavouriteListUseCase @Inject constructor(
    val repository: MovieRepository
) {

    suspend operator fun invoke() = repository.getFavouriteList()
}