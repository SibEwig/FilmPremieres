package com.sibewig.filmpremieres.domain

data class Movie(
    val id: Int,
    val name: String,
    val year: Int,
    val poster: String?,
    val description: String,
    val premiere: String
)
