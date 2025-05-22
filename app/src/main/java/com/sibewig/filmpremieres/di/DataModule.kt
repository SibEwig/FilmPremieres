package com.sibewig.filmpremieres.di

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
    }
}