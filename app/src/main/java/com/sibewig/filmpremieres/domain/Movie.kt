package com.sibewig.filmpremieres.domain

import java.time.LocalDate

data class Movie(
    val id: Int,
    val name: String,
    val year: Int,
    val poster: String?,
    val description: String,
    val premiere: LocalDate,
    val trailers: List<Trailer>?
)
