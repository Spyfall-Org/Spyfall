package com.dangerfield.features.consent

import android.app.Activity
import com.google.android.ump.ConsentInformation
import kotlinx.coroutines.flow.Flow

interface ConsentStatusRepository {
    fun getStatusFlow(activity: Activity,): Flow<ConsentStatus>
}

enum class ConsentStatus {
    ConsentGiven,
    ConsentDenied,
    ConsentNeeded,
    ConsentNotNeeded,
    Unknown
}

fun ConsentInformation.toConsentStatus() = when (consentStatus) {
    ConsentInformation.ConsentStatus.REQUIRED -> ConsentStatus.ConsentNeeded
    ConsentInformation.ConsentStatus.NOT_REQUIRED -> ConsentStatus.ConsentNotNeeded
    ConsentInformation.ConsentStatus.UNKNOWN -> ConsentStatus.Unknown
    ConsentInformation.ConsentStatus.OBTAINED -> {
        if (canRequestAds()) {
            ConsentStatus.ConsentGiven
        } else {
            ConsentStatus.ConsentDenied
        }
    }

    else -> ConsentStatus.Unknown
}