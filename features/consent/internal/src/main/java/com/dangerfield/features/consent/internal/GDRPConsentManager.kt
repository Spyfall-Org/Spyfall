package com.dangerfield.features.consent.internal

import android.app.Activity
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.ads.ui.InterstitialAd
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.ForceEEAConsentLocation
import com.dangerfield.features.consent.toConsentStatus
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.collectIn
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
import oddoneout.core.ApplicationStateRepository
import oddoneout.core.BuildInfo
import oddoneout.core.ApplicationState
import oddoneout.core.Catching
import oddoneout.core.showDebugSnack
import oddoneout.core.doNothing
import oddoneout.core.eitherWay
import oddoneout.core.logOnFailure
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
    private val gameResetInterstitialAd: InterstitialAd<OddOneOutAd.GameRestartInterstitial>,
    @ApplicationScope private val applicationScope: CoroutineScope,
    applicationStateRepository: ApplicationStateRepository
) {

    init {
        applicationStateRepository.applicationState().collectIn(applicationScope) {
            when (it) {
                ApplicationState.Foregrounded -> doNothing()
                ApplicationState.Backgrounded -> doNothing()
                ApplicationState.Destroyed -> gameResetInterstitialAd.remove()
            }
        }
    }

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

        // get the updated info and check if it matches the status we were provided
        getConsentStatus(activity, dispatch = false).getOrNull()?.let { expectedStatus ->
            if (expectedStatus != status) {
                val message =
                    "Status given: $status does not match expected status: $expectedStatus"
                Timber.e(message)
                showDebugSnack { message }
            }
        }
    }

    fun resetConsentStatus(activity: Activity) {
        if (!buildInfo.isDebug && !buildInfo.isQA) return
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.reset()
    }

    private suspend fun getConsentStatus(
        activity: Activity,
        dispatch: Boolean = true
    ): Catching<ConsentStatus> = Catching {
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
    }
        .onSuccess { status ->
            if (dispatch) {
                consentStatusFlow.value = status
            }
        }
        .onFailure {
            consentStatusFlow.value = ConsentStatus.Unknown
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

    fun shouldShowSettingsOption(activity: Activity): Boolean = Catching {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
        .logOnFailure()
        .getOrElse { false }

    private fun initializeAds(activity: Activity) = Catching {
        if (hasInitializedAds.getAndSet(true)) return@Catching

        Timber.d("Initializing Ads")

        gameResetInterstitialAd.load(activity)

        MobileAds.initialize(activity) {
            Timber.d("MobileAds initialized $it")
        }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("")).build()
        )
    }.logOnFailure()

    class ConsentFormError(message: String) : Throwable(message)
}
