package com.dangerfield.werewolf

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WerewolfApp : Application() {

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        setupFireStoreSettings()
    }

    private fun setupFireStoreSettings() {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        firebaseFirestore.firestoreSettings = settings
    }
}
