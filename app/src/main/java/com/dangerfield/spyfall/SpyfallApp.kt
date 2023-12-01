package com.dangerfield.spyfall

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dangerfield.features.blockingerror.blockingErrorRoute
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.fadeInToEndAnim
import com.dangerfield.libraries.navigation.fadeInToStartAnim
import com.dangerfield.libraries.navigation.fadeOutToEndAnim
import com.dangerfield.libraries.navigation.fadeOutToStartAnim
import com.dangerfield.libraries.navigation.internal.NavControllerRouter
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import spyfallx.ui.color.AccentColor

@Suppress("MagicNumber")
@Composable
fun SpyfallApp(
    isUpdateRequired: Boolean,
    accentColor: AccentColor,
    hadErrorLoadingApp: Boolean,
    navBuilderRegistry: NavBuilderRegistry
) {
    val navController = rememberNavController()

    val startingRoute = when {
        isUpdateRequired -> forcedUpdateNavigationRoute
        hadErrorLoadingApp -> blockingErrorRoute
        else -> welcomeNavigationRoute
    }

    SpyfallTheme(
        isDarkMode = isSystemInDarkTheme(),
        accentColor = accentColor.colorPrimitive
    ) {
        NavHost(
            navController = navController,
            startDestination = startingRoute.navRoute,
            enterTransition = { fadeInToStartAnim() },
            exitTransition = { fadeOutToStartAnim() },
            popEnterTransition = { fadeInToEndAnim() },
            popExitTransition = { fadeOutToEndAnim() }
        ) {

            navBuilderRegistry.registerNavBuilderForModule(
                navGraphBuilder = this,
                router = NavControllerRouter(navController = navController)
            )
        }
    }
}
