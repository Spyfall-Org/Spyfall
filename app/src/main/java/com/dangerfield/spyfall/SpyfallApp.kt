package com.dangerfield.spyfall

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.theme.SpyfallTheme

@Composable
fun SpyfallApp(
    isUpdateRequired: Boolean,
    navBuilderRegistry: NavBuilderRegistry
) {

    val navController = rememberNavController()

    SpyfallTheme(
        isDarkMode = isSystemInDarkTheme(),
        accentColor = ColorPrimitive.CherryPop700
    ) {
        NavHost(
            navController = navController,
            startDestination = if (true) forcedUpdateNavigationRoute else welcomeNavigationRoute,
        ) {
            navBuilderRegistry.registerNavBuilderForModule(this)
        }
    }
}
