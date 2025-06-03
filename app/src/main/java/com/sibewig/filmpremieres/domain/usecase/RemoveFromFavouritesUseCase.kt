package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class RemoveFromFavouritesUseCase @Inject constructor(
    val repository: MovieRepository
) {

    suspend operator fun invoke(movie: Movie) = repository.removeFromFavorites(movie)
}