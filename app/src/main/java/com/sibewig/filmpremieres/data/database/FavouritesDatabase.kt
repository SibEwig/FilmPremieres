package com.sibewig.filmpremieres.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [FavouriteEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FavouritesDatabase : RoomDatabase() {

    abstract fun favouriteDao(): FavouriteDao

    companion object {

        private var db: FavouritesDatabase? = null
        private const val DB_NAME = "favourites.db"
        private val LOCK = Any()

        fun getInstance(application: Application): FavouritesDatabase {
            db?.let {
                return it
            }
            synchronized(LOCK) {
                db?.let {
                    return it
                }
                return Room.databaseBuilder(
                    application,
                    FavouritesDatabase::class.java,
                    DB_NAME
                ).build().also {
                    db = it
                }
            }
        }
    }


}