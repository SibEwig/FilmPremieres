package com.sibewig.filmpremieres.data.network.model

import com.google.gson.annotations.SerializedName

data class TrailerListDto(
    @SerializedName("trailers")
    val trailers: List<TrailerDto>
)
