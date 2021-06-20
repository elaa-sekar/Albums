package com.task.albums

import android.app.Application
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AlbumsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initAllDependencies()
    }

    private fun initAllDependencies() {
        if (BuildConfig.DEBUG) {

            // Real Time Resource View/Analysis/Inspection
            Stetho.initializeWithDefaults(applicationContext)

            // Advanced Log
            Timber.plant(Timber.DebugTree())
        }
    }
}