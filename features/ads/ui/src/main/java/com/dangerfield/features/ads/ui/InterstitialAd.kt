package com.dangerfield.features.ads.ui

import android.content.Context
import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import oddoneout.core.Try
import oddoneout.core.doNothing
import oddoneout.core.findActivity
import oddoneout.core.logOnError
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class InterstitialAd<T : OddOneOutAd>(
    private val ad: T,
    private val adsConfig: AdsConfig,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider
) {

    private var rewardInterstitialAd: Deferred<RewardedInterstitialAd?>? = null

    fun load(context: Context) {
        rewardInterstitialAd = applicationScope.async(dispatcherProvider.main) {
            Try {
                loadRewardedInterstitialAd(context, ad.resId)
            }
                .logOnError()
                .getOrNull()
        }
    }

    suspend fun show(context: Context, onAdDismissed: () -> Unit) {
        val isSupposedToShow = adsConfig.isAdEnabled(ad)
        if (!isSupposedToShow) return

        val activity = context.findActivity()

        val ad = rewardInterstitialAd?.await()

        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    rewardInterstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    load(context)
                    onAdDismissed()
                }
            }

            ad.show(activity) {
                doNothing()
            }
        }
    }

    fun remove() {
        applicationScope.launch {
            rewardInterstitialAd?.cancelAndJoin()
            rewardInterstitialAd = null
        }
    }

    private suspend fun loadRewardedInterstitialAd(
        context: Context,
        adResId: Int
    ): RewardedInterstitialAd? {
        return if (!adsConfig.isAdEnabled(ad)) {
            null
        } else {
            suspendCancellableCoroutine { continuation ->
                RewardedInterstitialAd.load(
                    context,
                    context.getString(adResId),
                    AdManagerAdRequest.Builder().build(),
                    object : RewardedInterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Timber.e("InterstitialAd failed to load: ${adError.message}")
                            continuation.resumeWithException(IllegalStateException("InterstitialAd failed to load: ${adError.message}"))
                        }

                        override fun onAdLoaded(ad: RewardedInterstitialAd) {
                            continuation.resume(ad)
                        }
                    }
                )

                continuation.invokeOnCancellation {
                    doNothing() // for now
                }
            }
        }
    }
}