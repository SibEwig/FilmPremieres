package com.sibewig.filmpremieres.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val poster: String?,
    val premiere: LocalDate
)
