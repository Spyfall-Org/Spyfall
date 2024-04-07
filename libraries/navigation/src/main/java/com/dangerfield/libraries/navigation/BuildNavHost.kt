package com.dangerfield.libraries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/**
 * Interface for building the NavHost. This is used so that we can inject what we need into the builder
 * without making a mess of the calling app
 */
@Stable
interface BuildNavHost {
    
    @Composable
    operator fun invoke(startingRoute: String)
}
