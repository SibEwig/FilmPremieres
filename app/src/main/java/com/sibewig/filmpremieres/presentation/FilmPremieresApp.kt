package com.sibewig.filmpremieres.presentation

import android.app.Application
import com.sibewig.filmpremieres.di.ApplicationScope
import com.sibewig.filmpremieres.di.DaggerApplicationComponent

@ApplicationScope
class FilmPremieresApp: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}