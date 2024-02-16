package com.dangerfield.features.consent.internal

import android.app.Activity
import android.content.Context
import com.dangerfield.features.consent.ConsentStatus.*
import com.dangerfield.features.consent.OpenConsentForm
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import oddoneout.core.Try
import oddoneout.core.getOrElse
import oddoneout.core.logOnError
import oddoneout.core.throwIfDebug
import se.ansman.dagger.auto.AutoBind
import java.lang.IllegalStateException
import javax.inject.Inject


@AutoBind
@ActivityScoped
class OpenConsentFormImpl @Inject constructor(
    @ActivityContext private val context: Context,
    private val gdrpConsentManager: GDRPConsentManager,
    private val inHouseConsentManager: InHouseConsentManager
) : OpenConsentForm {

    override suspend fun invoke() {
        Try {
            val activity = context as Activity
            val gdrpConsentStatus = gdrpConsentManager.getConsentStatus(activity = activity)
                .getOrElse { Unknown }

            val inHouseConsentStatus = inHouseConsentManager.getConsentStatus()

            when(gdrpConsentStatus) {
                ConsentDenied,
                ConsentNeeded ->  gdrpConsentManager.showConsentForm(activity)

                ConsentNotNeeded,
                Unknown,
                ConsentGiven -> {
                    if (inHouseConsentStatus == ConsentNeeded) {
                        inHouseConsentManager.showConsentForm()
                    } else {
                        throw IllegalStateException("Consent not needed but was requested to be shown")
                    }
                }
            }
        }
            .throwIfDebug()
            .logOnError()
    }
}