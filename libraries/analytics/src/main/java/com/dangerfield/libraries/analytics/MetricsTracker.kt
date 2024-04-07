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

    /**
     *  A Metric representing an event where the user sees something.
     *  This could be a page, a view, or any other visual element.
     */
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

    /**
     * A Metric representing an interaction with the UI.
     * This could be a click, a swipe, or any other user action we care about.
     */
    sealed class Interaction(extras: Bundle) : Metric(extras) {
        class Click(
            val itemName: String,
            extras: Bundle = Bundle()
        ) : Interaction(extras)
    }

    /**
     * A Metric representing an event that doesn't fit into the other categories.
     * This could be a custom event, an error, or any other event we care about.
     */
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
    Other("other")
}