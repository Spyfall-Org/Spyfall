package com.dangerfield.spyfall.startup

import android.animation.ValueAnimator
import android.app.Activity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import oddoneout.core.BuildInfo
import oddoneout.core.Catching
import timber.log.Timber
import javax.inject.Inject

class SplashScreenBuilder @Inject constructor(
    private val buildInfo: BuildInfo
) {
    private var keepOnScreenCondition: () -> Boolean = { true }
    private var showLoadingCondition: () -> Boolean = { true }
    private lateinit var internalSplashScreen: androidx.core.splashscreen.SplashScreen

    fun keepOnScreenWhile(condition: () -> Boolean): SplashScreenBuilder {
        this.keepOnScreenCondition = condition
        return this
    }

    fun showLoadingWhen(condition: () -> Boolean): SplashScreenBuilder {
        this.showLoadingCondition = condition
        return this
    }

    @Suppress("MagicNumber")
    fun build(activity: Activity) = Catching {
        internalSplashScreen = activity.installSplashScreen()

        if (buildInfo.deviceName.contains("OnePlus", ignoreCase = true)) {
            // oneplus devices have issues with splash screens setOnExitAnimationListener which
            // we rely on for manually animating the splash screen
            internalSplashScreen.setKeepOnScreenCondition(keepOnScreenCondition)
        } else {
            // relies onsetOnExitAnimationListener to keep on screen
            internalSplashScreen.setKeepOnScreenCondition { false }

            internalSplashScreen.setOnExitAnimationListener { splashScreenViewProvider ->

                Catching {

                    val animator = splashScreenViewProvider.startIconRotation()

                    (activity as? LifecycleOwner)?.lifecycleScope?.launch {
                        var isSplashScreenUp = keepOnScreenCondition()
                        while (isSplashScreenUp) {
                            delay(500)
                            isSplashScreenUp = keepOnScreenCondition()
                        }

                        animator?.pause()
                        splashScreenViewProvider.remove()
                    } ?: run {
                        splashScreenViewProvider.remove()
                        Timber.e("SplashScreenBuilder: Activity is not a lifecycle owner, splash screen will be removed")
                    }
                }.onFailure {
                    splashScreenViewProvider.remove()
                }
            }
        }
    }.onFailure { Timber.e("SplashScreenBuilder: Failed to build splash screen. Exception caught: $it")}

    @Suppress("MagicNumber", "TooGenericExceptionCaught")
    private fun SplashScreenViewProvider.startIconRotation(): ValueAnimator? = try {
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.addUpdateListener { animation ->
            try {
                if (iconView != null) {
                    iconView.rotation = animation.animatedValue as Float
                }
            } catch (t: Throwable) {
                Timber.e("SplashScreenBuilder: icon view was null. Exception caught: $t")
            }
        }
        animator.duration = 1000 // duration for one rotation
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.start()
        animator
    } catch (t: Throwable) {
        Timber.e("SplashScreenBuilder: Failed to start icon rotation. Exception caught: $t")
        null
    }
}
