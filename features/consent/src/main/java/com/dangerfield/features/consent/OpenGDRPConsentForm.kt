package com.dangerfield.features.consent

import android.app.Activity
import oddoneout.core.Catching

/**
 * Opens the GDRP consent form
 * @param onlyIfNeeded if true, the form will only be shown if the user has not already given consent
 * and they are in a region that requires it
 */
interface OpenGDRPConsentForm {
    suspend operator fun invoke(onlyIfNeeded: Boolean): Catching<Unit>
}

/**
 * Checks if the GDRP consent form should have an option in the settings menu
 */
interface ShouldShowGDRPSettingsOption {
    operator fun invoke(): Boolean
}

/**
 * Resets the GDRP consent status. This is for debug purposes only and should not be used in production
 */
interface ResetGDRPConsent {
    operator fun invoke(activity: Activity)
}