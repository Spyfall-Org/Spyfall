package com.dangerfield.libraries.analytics

import android.os.Bundle

/**
 * Interface for logging analytics events.
 * usage:
 *
 * val tracker: SomeMetricTracker = SomeLogger()
 * tracker.log(Metric.Impression.Screen("some_screen"))
 */
interface MetricsTracker {
    fun log(metric: Metric)
}

sealed class Metric(val extras: Bundle) {

    sealed class Impression(extras: Bundle) : Metric(extras) {
        class Page(
            val pageName: String,
            val pageType: PageType,
            extras: Bundle = Bundle()
        ) : Impression(extras)

        class View(
            val viewName: String,
            extras: Bundle = Bundle()
        ) : Impression(extras)
    }

    sealed class Interaction(extras: Bundle) : Metric(extras) {
        class Click(
            val itemName: String,
            extras: Bundle = Bundle()
        ) : Interaction(extras)
    }

    sealed class Event(extras: Bundle) : Metric(extras) {
        class Custom(
            val eventName: String,
            extras: Bundle = Bundle()
        ) : Event(extras)

        class Error(
            val errorName: String,
            val throwable: Throwable?,
            extras: Bundle = Bundle()
        ) : Event(extras)
    }
}

enum class PageType(val value: String) {
    FullScreenPage("screen"),
    Dialog("modal"),
    BottomSheet("sheet"),
    OTHER("other")
}