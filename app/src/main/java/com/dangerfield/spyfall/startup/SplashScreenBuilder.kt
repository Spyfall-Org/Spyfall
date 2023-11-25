package com.dangerfield.spyfall.startup

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
            val loadingView = splashScreenViewProvider.addLoadingIndicator()
            (activity as? LifecycleOwner)?.lifecycleScope?.launch {
                var shouldShowLoading = showLoadingCondition()
                var isSplashScreenUp = keepOnScreenCondition()
                while (isSplashScreenUp) {
                    delay(500)
                    shouldShowLoading = showLoadingCondition()
                    isSplashScreenUp = keepOnScreenCondition()
                    if (!shouldShowLoading) {
                        splashScreenViewProvider.removeView(loadingView)
                    }
                }

                splashScreenViewProvider.remove()
            } ?: run {
                splashScreenViewProvider.remove()
                Timber.d("SplashScreenBuilder: Activity is not a lifecycle owner, splash screen will be removed")
            }
        }
    }

    private fun SplashScreenViewProvider.removeView(view: View?) {
        (this.view as? ViewGroup)?.removeView(view)

    }

    @Suppress("MagicNumber")
    private fun SplashScreenViewProvider.addLoadingIndicator(): View? {
        val viewGroup = this.view as? ViewGroup ?: return null
        val iconView = this.iconView

        val progressBar = ProgressBar(viewGroup.context).apply {
            isIndeterminate = true
            visibility = View.VISIBLE
        }

        val iconBottom = iconView.bottom
        val parentHeight = viewGroup.height
        val distanceToBottom = parentHeight - iconBottom

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        ).apply {
            bottomMargin = distanceToBottom / 2
        }

        progressBar.layoutParams = layoutParams
        progressBar.z = 10f

        viewGroup.addView(progressBar)
        return progressBar
    }
}
