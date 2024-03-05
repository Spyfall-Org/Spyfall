package com.dangerfield.features.consent

import android.app.Activity
import oddoneout.core.Try

interface OpenGDRPConsentForm {
    suspend operator fun invoke(onlyIfNeeded: Boolean): Try<Unit>
}

interface ShouldShowGDRPSettingsOption {
    operator fun invoke(): Boolean
}

interface ResetGDRPConsent {
    operator fun invoke(activity: Activity)
}