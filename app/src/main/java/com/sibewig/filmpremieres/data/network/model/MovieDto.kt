package com.sibewig.filmpremieres.data.network.model

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("year")
    val year: Int,
    @SerializedName("poster")
    val poster: PosterDto?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("premiere")
    val premiere: PremiereDto
)
