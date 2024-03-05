package com.dangerfield.features.consent.internal

import android.app.Activity
import com.dangerfield.features.consent.ResetGDRPConsent
import oddoneout.core.Try
import oddoneout.core.ignore
import oddoneout.core.ignoreValue
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class ResetGDRPConsentImpl @Inject constructor(
    private val gdrpConsentManager: GDRPConsentManager,
) : ResetGDRPConsent {

    override fun invoke(activity: Activity) = Try {
        gdrpConsentManager.resetConsentStatus(activity)
    }
        .logOnFailure()
        .ignore()
}