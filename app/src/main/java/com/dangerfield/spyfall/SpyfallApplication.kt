package com.dangerfield.spyfall

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.features.ads.ui.initializeAds
import com.dangerfield.libraries.coresession.internal.SessionRepository
import com.dangerfield.libraries.logging.RemoteLogger
import com.dangerfield.spyfall.legacy.di.legacySpyfallModules
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

    // TODO see if initializing analytics or app check later or something would be better
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
        firebaseAnalytics.setSessionTimeoutDuration(
            SessionRepository.SESSION_MAXIMUM_TIME_AWAY.inWholeMilliseconds
        )

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: androidx.lifecycle.LifecycleOwner) {
                super.onStart(owner)
                applicationStateRepository.onAppStart()
            }

            override fun onStop(owner: androidx.lifecycle.LifecycleOwner) {
                super.onStop(owner)
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

        initializeAds()
    }

    private fun setupFireStoreSettings() {
        val db: FirebaseFirestore by inject()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
    }

}
