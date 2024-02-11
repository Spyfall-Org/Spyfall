package com.dangerfield.spyfall.startup

import android.animation.ValueAnimator
import android.app.Activity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import oddoneout.core.Try
import timber.log.Timber

class SplashScreenBuilder(private val activity: Activity) {
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
    fun build() {
        internalSplashScreen = activity.installSplashScreen()

        // relies onsetOnExitAnimationListener to keep on screen
        internalSplashScreen.setKeepOnScreenCondition { false }

        internalSplashScreen.setOnExitAnimationListener { splashScreenViewProvider ->

            Try {

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

    @Suppress("MagicNumber", "TooGenericExceptionCaught")
    private fun SplashScreenViewProvider.startIconRotation(): ValueAnimator? = try {
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.addUpdateListener { animation ->
            iconView.rotation = animation.animatedValue as Float
        }
        animator.duration = 1000 // duration for one rotation
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.start()
        animator
    } catch (t: Throwable) {
        Timber.e("SplashScreenBuilder: Failed to start icon rotation: $t")
        null
    }
}
