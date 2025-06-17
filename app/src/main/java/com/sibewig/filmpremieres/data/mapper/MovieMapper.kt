package com.sibewig.filmpremieres.data.mapper

import com.sibewig.filmpremieres.data.database.FavouriteEntity
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
        mapPremiereDtoDateToLocalDate(dto.premiere),
        dto.trailerList?.trailers?.map { mapTrailerDtoToDomain(it) }
    )

    private fun mapPremiereDtoDateToLocalDate(dto: PremiereDto?): LocalDate {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = dto?.date?.substring(0, 10) ?: "1986-11-15"
        return LocalDate.parse(date, dateFormatter)

    }

    fun mapFavouriteEntityToMovie(favouriteEntity: FavouriteEntity) = Movie(
        favouriteEntity.id,
        favouriteEntity.name,
        0,
        favouriteEntity.poster,
        "",
        favouriteEntity.premiere,
        null
    )

    fun mapMovieToFavourite(movie: Movie) = FavouriteEntity(
        movie.id,
        movie.name,
        movie.poster,
        movie.premiere
    )

    private fun mapTrailerDtoToDomain(dto: TrailerDto) = Trailer(dto.name, dto.url)


    companion object {

        private const val EMPTY_DESCRIPTION = "Описание отсутствует"
    }


}