package com.dangerfield.libraries.analytics

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode
import com.dangerfield.libraries.navigation.Route

/**
 * Effect that logs a page impression when the composable is first composed.
 * @param route The route of the page being logged
 * @param type The type of page being logged
 * @param extras Any additional data to be logged with the page impression
 */
@Composable
fun PageLogEffect(
    route: Route.Template,
    type: PageType,
    extras: Bundle = Bundle()
) {
    if (LocalInspectionMode.current) return // no logging in previews

    val metricsTracker = LocalMetricsTracker.current

    LaunchedEffect(Unit) {
        metricsTracker.log(
            Metric.Impression.Page(
                pageName = route.navRoute.substringBefore("?"),
                pageType = type,
                extras = extras
            )
        )
    }
}