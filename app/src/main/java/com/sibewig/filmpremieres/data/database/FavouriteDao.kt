package com.sibewig.filmpremieres.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(movie: FavouriteEntity)

    @Delete
    suspend fun removeFromFavorites(movie: FavouriteEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT * FROM favourites")
    suspend fun getAllFavorites(): List<FavouriteEntity>

}
