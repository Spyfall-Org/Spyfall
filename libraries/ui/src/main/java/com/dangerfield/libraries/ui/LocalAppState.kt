package com.dangerfield.libraries.ui

import androidx.compose.runtime.staticCompositionLocalOf
import oddoneout.core.AppState

val LocalAppState = staticCompositionLocalOf<AppState> {
    error("No LocalAppState provided")
}