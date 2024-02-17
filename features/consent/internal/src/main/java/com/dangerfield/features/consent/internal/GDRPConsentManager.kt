package com.dangerfield.features.consent.internal

import android.app.Activity
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.dangerfield.libraries.coreflowroutines.childSupervisorScope
import com.dangerfield.libraries.ui.getBoldSpan
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import oddoneout.core.BuildInfo
import oddoneout.core.Try
import oddoneout.core.developerSnackIfDebug
import oddoneout.core.logOnError
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class GDRPConsentManager @Inject constructor(
    private val buildInfo: BuildInfo,
    private val forceEEAConsentLocation: ForceEEAConsentLocation,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {

    private val hasInitializedAds = AtomicBoolean(false)

    private val consentStatusFlow = MutableStateFlow<ConsentStatus?>(null)

    fun getConsentStatusFlow(activity: Activity): Flow<ConsentStatus> {
        refreshStatus(activity)
        return consentStatusFlow.filterNotNull()
    }

    private fun refreshStatus(activity: Activity) {
        applicationScope.launch {
            getConsentStatus(activity, dispatch = true)
        }
    }

    suspend fun updateConsentStatus(activity: Activity, status: ConsentStatus) {
        consentStatusFlow.value = status
        getConsentStatus(activity, dispatch = false).getOrNull()?.let { expectedStatus ->
            if (expectedStatus != status) {
                val message =
                    "Status given: $status does not match expected status: $expectedStatus"
                Timber.e(message)
                developerSnackIfDebug { message }
            }
        }
    }

    suspend fun getConsentStatus(
        activity: Activity,
        dispatch: Boolean = true
    ): Try<ConsentStatus> = Try {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setForceTesting(buildInfo.isDebug)

        if (forceEEAConsentLocation()) {
            debugSettings.setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
        }

        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings.build())
            .build()

        getStatusAsync(
            consentInformation = consentInformation,
            activity = activity,
            params = params,
        )
    }.onSuccess { status ->
        if (dispatch) {
            consentStatusFlow.value = status
        }
    }

    private suspend fun getStatusAsync(
        consentInformation: ConsentInformation,
        activity: Activity,
        params: ConsentRequestParameters,
    ): ConsentStatus {
        return suspendCancellableCoroutine { continuation ->
            consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                {
                    if (consentInformation.canRequestAds()) {
                        initializeAds(activity)
                    }
                    Timber.d("Loading GDRP Consent status")
                    continuation.resume(consentInformation.toConsentStatus())
                },
                { requestConsentError: FormError ->
                    Timber.d("GDRP Consent Status Load failed: ${requestConsentError.message}")
                    continuation.resumeWithException(
                        ConsentFormError(requestConsentError.message)
                    )
                }
            )

            initializeAds(activity)
        }
    }


    fun showConsentForm(activity: Activity) = Try {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        UserMessagingPlatform.loadAndShowConsentFormIfRequired(
            activity
        ) { loadAndShowError ->

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
                updateConsentStatus(activity = activity, status = consentInformation.toConsentStatus())
            }
        }
    }.logOnError()

    private fun ConsentInformation.toConsentStatus() = when (consentStatus) {
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


    private fun initializeAds(activity: Activity) = Try {
        if (hasInitializedAds.getAndSet(true)) return@Try

        Timber.d("Initializing Ads")
        MobileAds.initialize(activity) {
            Timber.d("MobileAds initialized $it")
        }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("")).build()
        )
    }.logOnError()

    class ConsentFormError(message: String) : Throwable(message)
}