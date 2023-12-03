package com.dangerfield.libraries.navigation.bottomsheet

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.get
import com.dangerfield.libraries.navigation.slideDownToExitBottomSheet
import com.dangerfield.libraries.navigation.slideUpToEnterBottomSheet
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug

fun NavHostController.getBottomSheetNavigator(): BottomSheetNavigator? = Try {
    navigatorProvider.get<Navigator<out NavDestination>>(
        BottomSheetNavigator.NAME
    ) as? BottomSheetNavigator ?: throw IllegalStateException("No bottom sheet navigator")
}
    .logOnError("No bottom sheet navigator found")
    .throwIfDebug()
    .getOrNull()

/**
 * Allows for a bottom sheet to be shown as a destination in the navigation graph
 * The bottom sheet is expanded automatically when navigated to
 *
 * Note: When using this, popBackStack but be called from onDismissRequest
 */
fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = {
        slideUpToEnterBottomSheet()
    },
    exitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = {
        slideDownToExitBottomSheet()
    },
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {

    addDestination(
        BottomSheetNavigator.Destination(
            provider[BottomSheetNavigator::class],
            content
        ).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
            this.enterTransition = enterTransition
            this.exitTransition = exitTransition
        }
    )
}