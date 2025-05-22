package com.sibewig.filmpremieres.di

import com.sibewig.filmpremieres.presentation.MainActivity
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {

        fun create(): ApplicationComponent
    }
}