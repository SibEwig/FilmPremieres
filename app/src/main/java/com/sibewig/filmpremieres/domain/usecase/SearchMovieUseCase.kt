package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke(query: String) = repository.searchMovie(query)
}