package com.dangerfield.spyfall

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.spyfall.legacy.di.legacySpyfallModules
import com.dangerfield.spyfall.legacy.util.RemoveUserTimer
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.ERROR
import org.koin.core.logger.Level.NONE

@HiltAndroidApp
class SpyfallApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) ERROR else NONE)
            androidContext(this@SpyfallApp)
            modules(legacySpyfallModules)
        }

        setupFireStoreSettings()
        setupGameLeftObserver()
    }

    private fun setupFireStoreSettings() {
        val db: FirebaseFirestore by inject()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
    }

    private fun setupGameLeftObserver() {
        val removeUserTimer: RemoveUserTimer by inject()
        ProcessLifecycleOwner.get().lifecycle.addObserver(removeUserTimer)
    }
}
