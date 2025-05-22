package com.sibewig.filmpremieres.di

import androidx.lifecycle.ViewModel
import com.sibewig.filmpremieres.presentation.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindViewModel(impl: MainViewModel): ViewModel
}