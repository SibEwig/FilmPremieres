package com.sibewig.filmpremieres.domain.usecase

import com.sibewig.filmpremieres.domain.MovieRepository
import javax.inject.Inject

class GetFullListLoadedUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    operator fun invoke() = repository.fullListLoaded
}