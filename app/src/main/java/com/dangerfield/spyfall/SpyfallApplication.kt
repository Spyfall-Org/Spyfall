package com.dangerfield.spyfall

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.libraries.logging.RemoteLogger
import com.dangerfield.spyfall.legacy.di.legacySpyfallModules
import com.dangerfield.spyfall.legacy.util.RemoveUserTimer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.ERROR
import org.koin.core.logger.Level.NONE
import spyfallx.core.ApplicationStateRepository
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SpyfallApplication : Application() {

    @Inject
    lateinit var applicationStateRepository: ApplicationStateRepository

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    private val lifecycle get() = ProcessLifecycleOwner.get().lifecycle

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCreate() {
        super.onCreate()

        // TODO look into permission requesting for analytics collection
        firebaseAnalytics.setAnalyticsCollectionEnabled(BuildConfig.DEBUG)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: androidx.lifecycle.LifecycleOwner) {
                applicationStateRepository.onAppStart()
            }

            override fun onStop(owner: androidx.lifecycle.LifecycleOwner) {
                applicationStateRepository.onAppStop()
            }
        })

        Timber.plant(RemoteLogger())

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) ERROR else NONE)
            androidContext(this@SpyfallApplication)
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
