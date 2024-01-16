package com.dangerfield.libraries.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalMetricsTracker = staticCompositionLocalOf<MetricsTracker> {
    error("No MetricsTracker provided")
}