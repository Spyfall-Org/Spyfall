package com.dangerfield.features.consent

import android.app.Activity

interface OpenGDRPConsentForm {
    suspend operator fun invoke(onlyIfNeeded: Boolean)
}

interface ShouldShowGDRPSettingsOption {
    operator fun invoke(): Boolean
}

interface ResetGDRPConsent {
    operator fun invoke(activity: Activity)
}