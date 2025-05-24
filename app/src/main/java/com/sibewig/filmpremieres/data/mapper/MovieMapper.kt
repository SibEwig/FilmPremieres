package com.sibewig.filmpremieres.data.mapper

import com.sibewig.filmpremieres.data.network.model.MovieDto
import com.sibewig.filmpremieres.data.network.model.PremiereDto
import com.sibewig.filmpremieres.data.network.model.TrailerDto
import com.sibewig.filmpremieres.domain.Movie
import com.sibewig.filmpremieres.domain.Trailer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MovieMapper @Inject constructor() {

    fun mapMovieDtoToDomain(dto: MovieDto) = Movie(
        dto.id,
        dto.name,
        dto.year,
        dto.poster?.url,
        dto.description ?: EMPTY_DESCRIPTION,
        mapPremiereDtoDateToString(dto.premiere),
        dto.trailers?.map { mapTrailerDtoToDomain(it) }
    )

    private fun mapPremiereDtoDateToString(dto: PremiereDto): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")
        val date = LocalDate.parse(dto.date.substring(0, 10), inputFormatter)
        return date.format(outputFormatter)
    }

    private fun mapTrailerDtoToDomain(dto: TrailerDto) = Trailer(dto.name, dto.url)


    companion object {

        private const val EMPTY_DESCRIPTION = "Описание отсутствует"
    }


}