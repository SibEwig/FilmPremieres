package com.sibewig.filmpremieres.data.mapper

import com.sibewig.filmpremieres.data.network.model.MovieDto
import com.sibewig.filmpremieres.data.network.model.PremiereDto
import com.sibewig.filmpremieres.data.network.model.TrailerDto
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.Trailer
import javax.inject.Inject

class MovieMapper @Inject constructor() {

    fun mapMovieDtoToDomain(dto: MovieDto) = Movie(
        dto.id,
        dto.name,
        dto.year,
        dto.poster?.url,
        dto.description ?: EMPTY_DESCRIPTION,
        mapPremiereDtoDateToString(dto.premiere)
    )

    private fun mapPremiereDtoDateToString(dto: PremiereDto): String = dto.date.substring(0, 10)

    fun mapListMovieDtoToDomain(dto: List<MovieDto>) = dto.map { mapMovieDtoToDomain(it) }

    private fun mapTrailerDtoToDomain(dto: TrailerDto) = Trailer(dto.name, dto.url)


    companion object {

        private const val EMPTY_DESCRIPTION = "Описание отсутствует"
    }


}