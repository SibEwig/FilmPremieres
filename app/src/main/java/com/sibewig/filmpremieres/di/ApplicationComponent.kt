package com.sibewig.filmpremieres.di

import android.app.Application
import com.sibewig.filmpremieres.presentation.MainActivity
import com.sibewig.filmpremieres.presentation.MovieDetailActivity
import dagger.BindsInstance
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

    fun inject(activity: MovieDetailActivity)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}