package com.dangerfield.spyfall.di

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.RemoveUserTimer
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class KoinApp : Application(), LifecycleObserver{
    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this, getString(R.string.ads_mod_app_id));

        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@KoinApp)
            modules(mainModule)
        }

        setupGameLeftObserver()
    }

    private fun setupGameLeftObserver() {
        val removeUserTimer : RemoveUserTimer by inject()
        ProcessLifecycleOwner.get().lifecycle.addObserver(removeUserTimer)
    }
}