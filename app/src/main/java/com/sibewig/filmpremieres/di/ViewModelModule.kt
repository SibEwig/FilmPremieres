package com.sibewig.filmpremieres.di

import androidx.lifecycle.ViewModel
import com.sibewig.filmpremieres.presentation.MainViewModel
import com.sibewig.filmpremieres.presentation.MovieDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    @Binds
    fun bindMovieDetailViewModel(impl: MovieDetailViewModel): ViewModel
}