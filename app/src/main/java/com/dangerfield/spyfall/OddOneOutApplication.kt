package com.dangerfield.spyfall

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.dangerfield.features.ads.ui.initializeAds
import com.dangerfield.libraries.coresession.internal.SessionRepository
import com.dangerfield.libraries.logging.RemoteLogger
import com.dangerfield.spyfall.free.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import oddoneout.core.ApplicationStateRepository
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class OddOneOutApplication : Application() {

    @Inject
    lateinit var applicationStateRepository: ApplicationStateRepository

    // TODO see if initializing analytics or app check later or something would be better
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    private val lifecycle get() = ProcessLifecycleOwner.get().lifecycle

    init {
        if (BuildConfig.DEBUG || BuildConfig.IS_QA) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCreate() {
        super.onCreate()

        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
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

        setupFireStoreSettings()

        initializeAds()
    }

    private fun setupFireStoreSettings() {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        firebaseFirestore.firestoreSettings = settings
    }
}
