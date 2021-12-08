package com.baiganov.fintech

import android.app.Application
import com.baiganov.fintech.di.DaggerAppComponent

class App : Application() {

    val component by lazy {
        DaggerAppComponent.factory()
            .create(this)
    }
}