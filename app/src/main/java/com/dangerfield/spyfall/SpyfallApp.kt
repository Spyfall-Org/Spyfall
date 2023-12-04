package com.dangerfield.spyfall

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dangerfield.features.blockingerror.blockingErrorRoute
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.bottomsheet.BottomSheetHost
import com.dangerfield.libraries.navigation.bottomsheet.BottomSheetNavigator
import com.dangerfield.libraries.navigation.bottomsheet.getBottomSheetNavigator
import com.dangerfield.libraries.navigation.fadeInToEndAnim
import com.dangerfield.libraries.navigation.fadeInToStartAnim
import com.dangerfield.libraries.navigation.fadeOutToEndAnim
import com.dangerfield.libraries.navigation.fadeOutToStartAnim
import com.dangerfield.libraries.navigation.internal.NavControllerRouter
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import spyfallx.ui.color.AccentColor

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Suppress("MagicNumber")
@Composable
fun SpyfallApp(
    accentColor: AccentColor,
    navBuilderRegistry: NavBuilderRegistry,
    isUpdateRequired: Boolean,
    hasBlockingError: Boolean,
) {
    val navController = rememberNavController(BottomSheetNavigator())
    val coroutineScope = rememberCoroutineScope()
    val router = remember {
        NavControllerRouter(
            navController = navController,
            coroutineScope = coroutineScope
        )
    }

    val startingRoute = when {
        isUpdateRequired -> forcedUpdateNavigationRoute
        hasBlockingError -> blockingErrorRoute
        else -> welcomeNavigationRoute
    }
    // TODO add maintainence mode
    // maybe have main activity view model observe the config and update the app state.
    // having an app state here would be great

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
                router = router
            )
        }

        val bottomSheetNavigator = navController.getBottomSheetNavigator()

        bottomSheetNavigator?.let {
            BottomSheetHost(bottomSheetNavigator)
        }
    }
}
