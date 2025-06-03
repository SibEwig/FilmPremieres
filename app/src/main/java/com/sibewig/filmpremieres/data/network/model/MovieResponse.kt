package com.sibewig.filmpremieres.data.network.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("docs")
    val movies: List<MovieDto>,
    @SerializedName("total")
    val total: Int
)
