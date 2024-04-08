package com.dangerfield.libraries.analytics.internal

import android.os.Bundle
import androidx.core.os.bundleOf
import com.dangerfield.libraries.analytics.Metric
import com.dangerfield.libraries.analytics.MetricsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import oddoneout.core.BuildInfo
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.throwIfDebug
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AutoBind
class FirebaseMetricsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val buildInfo: BuildInfo
) : MetricsTracker {

    override fun log(metric: Metric) {
        when (metric) {
            is Metric.Event.Custom -> logCustom(metric)
            is Metric.Event.Error -> logError(metric)
            is Metric.Impression.Page -> logScreenImpression(metric)
            is Metric.Impression.View -> logViewImpression(metric)
            is Metric.Interaction.Click -> logClick(metric)
        }
    }

    private fun logCustom(metric: Metric.Event.Custom) {
        val bundle = bundleOf()

        bundle.putAll(metric.extras)

        log(metric.eventName, bundle)
    }

    private fun logError(metric: Metric.Event.Error) {
        val bundle = bundleOf(
            ERROR_NAME to metric.errorName,
        )

        metric.throwable?.let {
            bundle.putString(ERROR_MESSAGE, it.message)
            bundle.putString(ERROR_CODE, it.javaClass.simpleName)
        }

        bundle.putAll(metric.extras)

        log(ERROR_EVENT, bundle)
    }

    private fun logScreenImpression(metric: Metric.Impression.Page) {
        val bundle = bundleOf(
            PAGE_NAME to metric.pageName,
            PAGE_TYPE to metric.pageType.value
        )

        bundle.putAll(metric.extras)

        log(PAGE_VIEWED, bundle)
    }

    private fun logViewImpression(metric: Metric.Impression.View) {
        val bundle = bundleOf(
            VIEW_NAME to metric.viewName,
        )

        bundle.putAll(metric.extras)

        log(VIEW_VIEWED, bundle)
    }

    private fun logClick(metric: Metric.Interaction.Click) {
        val bundle = bundleOf(
            ITEM_NAME to metric.itemName,
        )

        bundle.putAll(metric.extras)

        log(ITEM_CLICKED, bundle)
    }

    private fun log(key: String, bundle: Bundle) {
        bundle.addSharedMetricValues()

        val bundleString = bundle.keySet().joinToString(", ") { "$it: ${bundle[it]}" }
        Timber.i("Metrics Event: $key\n$bundleString")

        firebaseAnalytics.logEvent(key.toFirebaseKey(), bundle)
    }

    private fun Bundle.addSharedMetricValues() {
        putAll(
            bundleOf(
                APP_VERSION to buildInfo.versionName,
                PLATFORM to PLATFORM_ANDROID,
            )
        )
    }

    /**
     * All Firebase keys must consist of letters, digits or _ (underscores).
     * Should Only contain alphanumeric characters and underscores (_).
     * Must be between 1- 40 characters
     */
    private fun String.toFirebaseKey(): String {
        return trim()
            .replace(" ", "_")
            .filter { it.isLetterOrDigit() || it == '_' }
            .take(MAX_KEY_LENGTH)
            .ifEmpty {
                throwIfDebug(IllegalArgumentException("Invalid Firebase key: $this"))
                DEFAULT_KEY
            }
    }

    companion object {
        private const val MAX_KEY_LENGTH = 40
        private const val DEFAULT_KEY = "Invalid_Key"
        private const val ERROR_NAME = "error_name"
        private const val ERROR_MESSAGE = "error_message"
        private const val ERROR_CODE = "error_code"
        private const val PAGE_NAME = "page_name"
        private const val VIEW_NAME = "view_name"
        private const val VIEW_VIEWED = "view_viewed"
        private const val ITEM_NAME = "item_name"
        private const val ITEM_CLICKED = "item_clicked"
        private const val PAGE_VIEWED = "page_viewed"
        private const val ERROR_EVENT = "error_event"
        private const val PAGE_TYPE = "page_type"
        private const val PLATFORM = "platform"
        private const val APP_VERSION = "app_version"
        private const val PLATFORM_ANDROID = "android"
    }
}
