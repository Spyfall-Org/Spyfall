package com.dangerfield.spyfall.di

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.RemoveUserTimer
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class KoinApp : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@KoinApp)
            modules(mainModule)
        }

        setupFireStoreSettings()
        setupGameLeftObserver()
        setupAds()
    }

    private fun setupAds() {
        MobileAds.initialize(this, getString(R.string.ads_mod_app_id));
    }

    private fun setupFireStoreSettings() {
        val db: FirebaseFirestore by inject()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
    }

    private fun setupGameLeftObserver() {
        val removeUserTimer : RemoveUserTimer by inject()
        ProcessLifecycleOwner.get().lifecycle.addObserver(removeUserTimer)
    }
}