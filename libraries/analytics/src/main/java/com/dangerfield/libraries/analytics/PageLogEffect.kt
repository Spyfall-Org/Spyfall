package com.dangerfield.libraries.analytics

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode
import com.dangerfield.libraries.navigation.Route

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