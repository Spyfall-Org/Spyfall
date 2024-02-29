package com.dangerfield.libraries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface BuildNavHost {
    
    @Composable
    operator fun invoke(startingRoute: String)
}
