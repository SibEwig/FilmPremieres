package com.sibewig.filmpremieres.di

import android.app.Application
import com.sibewig.filmpremieres.data.database.FavouriteDao
import com.sibewig.filmpremieres.data.database.FavouritesDatabase
import com.sibewig.filmpremieres.data.network.ApiFactory
import com.sibewig.filmpremieres.data.network.ApiService
import com.sibewig.filmpremieres.data.repository.RepositoryImpl
import com.sibewig.filmpremieres.domain.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindMovieRepository(impl: RepositoryImpl): MovieRepository

    companion object {

        @Provides
        @ApplicationScope
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }

        @Provides
        @ApplicationScope
        fun provideFavouriteDao(
            application: Application
        ): FavouriteDao {
            return FavouritesDatabase.getInstance(application).favouriteDao()
        }
    }
}