package com.dangerfield.spyfall.di

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.spyfall.util.RemoveUserTimer
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class KoinApp : Application(), LifecycleObserver{
    override fun onCreate() {
        super.onCreate()

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