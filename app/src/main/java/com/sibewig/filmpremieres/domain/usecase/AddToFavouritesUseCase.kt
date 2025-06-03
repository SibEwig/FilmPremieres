package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class AddToFavouritesUseCase @Inject constructor(
    val repository: MovieRepository
) {

    suspend operator fun invoke(movie: Movie) = repository.addToFavorites(movie)
}