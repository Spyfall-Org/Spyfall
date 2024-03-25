package com.dangerfield.features.consent

import android.app.Activity
import oddoneout.core.Catching

interface OpenGDRPConsentForm {
    suspend operator fun invoke(onlyIfNeeded: Boolean): Catching<Unit>
}

interface ShouldShowGDRPSettingsOption {
    operator fun invoke(): Boolean
}

interface ResetGDRPConsent {
    operator fun invoke(activity: Activity)
}