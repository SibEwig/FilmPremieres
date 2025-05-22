package com.sibewig.filmpremieres.domain

import javax.inject.Inject

class GetMovieInfoUseCase @Inject constructor(
    private val repository: MovieRepository
) {

    suspend operator fun invoke(id: Int) = repository.getMovieInfo(id)
}