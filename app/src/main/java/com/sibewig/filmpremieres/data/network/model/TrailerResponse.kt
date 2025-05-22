package com.sibewig.filmpremieres.data.network.model

import com.google.gson.annotations.SerializedName

data class TrailerResponse(
    @SerializedName("videos")
    val trailerList: TrailerListDto
)
