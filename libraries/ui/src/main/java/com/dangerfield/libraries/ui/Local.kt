package com.dangerfield.libraries.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.Colors
import com.dangerfield.libraries.ui.typography.Typography
import oddoneout.core.AppState
import oddoneout.core.BuildInfo

internal val LocalColors = compositionLocalOf<Colors> {
    error("OddOneOutTheme wasn't applied")
}

val LocalContentColor = compositionLocalOf<ColorResource> {
    error("OddOneOutTheme wasn't applied")
}

val LocalTypography = compositionLocalOf<Typography> {
    error("OddOneOutTheme wasn't applied")
}

val LocalBuildInfo = staticCompositionLocalOf<BuildInfo> {
    error("No LocalBuildInfo provided")
}

val LocalAppState = staticCompositionLocalOf<AppState> {
    error("No LocalAppState provided")
}