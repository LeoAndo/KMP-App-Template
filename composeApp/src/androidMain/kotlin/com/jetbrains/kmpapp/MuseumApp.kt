package com.jetbrains.kmpapp

import android.app.Application
import com.jetbrains.kmpapp.di.initKoin

class MuseumApp : Application() {
    companion object {
        lateinit var instance: MuseumApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initKoin()
    }
}
