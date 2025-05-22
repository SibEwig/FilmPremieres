package com.sibewig.filmpremieres.data.network.model

import com.google.gson.annotations.SerializedName

data class PremiereDto(
    @SerializedName("russia")
    val date: String
)
