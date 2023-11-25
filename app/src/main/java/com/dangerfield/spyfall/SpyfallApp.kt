package com.dangerfield.spyfall

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dangerfield.features.blockingerror.blockingErrorBaseRoute
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import com.dangerfield.libraries.ui.color.ColorPrimitive
import spyfallx.ui.theme.SpyfallTheme

@Suppress("MagicNumber")
@Composable
fun SpyfallApp(
    isUpdateRequired: Boolean,
    hadErrorLoadingApp: Boolean,
    navBuilderRegistry: NavBuilderRegistry
) {
    val navController = rememberNavController()
    val startingRoute = when {
        isUpdateRequired -> forcedUpdateNavigationRoute
        hadErrorLoadingApp -> blockingErrorBaseRoute
        else -> welcomeNavigationRoute
    }

    SpyfallTheme(
        isDarkMode = isSystemInDarkTheme(),
        accentColor = ColorPrimitive.CherryPop700
    ) {
        NavHost(
            navController = navController,
            startDestination = startingRoute,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            navBuilderRegistry.registerNavBuilderForModule(
                navGraphBuilder = this,
                navigationController = navController
            )
        }
    }
}
