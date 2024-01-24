package com.dangerfield.libraries.ui

import androidx.compose.runtime.staticCompositionLocalOf
import oddoneout.core.BuildInfo

val LocalBuildInfo = staticCompositionLocalOf<BuildInfo> {
    error("No LocalBuildInfo provided")
}