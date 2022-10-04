package com.dangerfield.spyfall.di

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.spyfall.di.modules.spyfallModules
import com.dangerfield.spyfall.util.RemoveUserTimer
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.*

class KoinApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) ERROR else NONE)
            androidContext(this@KoinApp)
            modules(spyfallModules)
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