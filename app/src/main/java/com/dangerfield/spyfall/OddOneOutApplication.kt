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
import com.google.firebase.firestore.LocalCacheSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.android.HiltAndroidApp
import oddoneout.core.ApplicationStateRepository
import oddoneout.core.Try
import oddoneout.core.logOnError
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class OddOneOutApplication : Application() {

    @Inject
    lateinit var applicationStateRepository: ApplicationStateRepository

    // TODO see if initializing analytics or app check later or something would be better
    @Inject
    lateinit var firebaseAnalytics: Provider<FirebaseAnalytics>

    @Inject
    lateinit var firebaseFirestore: Provider<FirebaseFirestore>

    private val lifecycle get() = ProcessLifecycleOwner.get().lifecycle

    init {
        if (BuildConfig.DEBUG || BuildConfig.IS_QA) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCreate() {
        super.onCreate()

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

        Try {
            setupFireStore()
        }
            .logOnError()

        initializeAds()
    }

    private fun setupFireStore() {
        val analytics = firebaseAnalytics.get()
        val firestore = firebaseFirestore.get()

        analytics.setAnalyticsCollectionEnabled(true)
        analytics.setSessionTimeoutDuration(
            SessionRepository.SESSION_MAXIMUM_TIME_AWAY.inWholeMilliseconds
        )

        firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = false
        }
    }
}
