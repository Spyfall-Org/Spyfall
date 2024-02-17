package com.dangerfield.features.consent.internal

import android.app.Activity
import android.content.Context
import com.dangerfield.features.consent.OpenGDRPConsentForm
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import oddoneout.core.Try
import oddoneout.core.logOnError
import oddoneout.core.throwIfDebug
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject

@AutoBind
@ActivityScoped
class OpenGDRPGDRPConsentFormImpl @Inject constructor(
    @ActivityContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val gdrpConsentManager: GDRPConsentManager,
) : OpenGDRPConsentForm {

    override suspend fun invoke(onlyIfNeeded: Boolean) {
        Try {
            Timber.d("Opening GDRP Consent Form")
            val activity = context as Activity
            val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
            val dismissListener = getDismissListener(consentInformation, activity)
            if (onlyIfNeeded) {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity,
                    dismissListener,
                )
            } else {
                UserMessagingPlatform.showPrivacyOptionsForm(
                    activity,
                    dismissListener
                )
            }
        }
            .throwIfDebug()
            .logOnError()
    }

    private fun getDismissListener(
        consentInformation: ConsentInformation,
        activity: Activity
    ) = ConsentForm.OnConsentFormDismissedListener { loadAndShowError ->
        Timber.e(
            """
                        Consent Form Dismissed:
                        Error Present: ${loadAndShowError != null}
                        Error Message: ${loadAndShowError?.message}
                        Error Code: ${loadAndShowError?.errorCode}
                        Can Request Ads: ${consentInformation.canRequestAds()}
                        ConsentStatus: ${
                when (consentInformation.consentStatus) {
                    ConsentInformation.ConsentStatus.UNKNOWN -> "UNKNOWN"
                    ConsentInformation.ConsentStatus.REQUIRED -> "REQUIRED"
                    ConsentInformation.ConsentStatus.NOT_REQUIRED -> "NOT_REQUIRED"
                    ConsentInformation.ConsentStatus.OBTAINED -> "OBTAINED"
                    else -> "UNKNOWN"
                }
            }
                    """.trimIndent()
        )

        applicationScope.launch {
            gdrpConsentManager.updateConsentStatus(
                activity = activity,
                consentInformation = consentInformation
            )
        }
    }
}