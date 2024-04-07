package com.dangerfield.libraries.navigation.floatingwindow

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.get

/**
 * Allows for a bottom sheet to be shown as a destination in the navigation graph
 * The bottom sheet is expanded automatically when navigated to
 *
 * Note: When using this, pop back stack does not dismiss,
 * the sheet state needs to be used to dismiss before
 * popping if you want it to be pretty
 */
fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        FloatingWindowNavigator.Destination(
            provider[FloatingWindowNavigator::class],
            content
        ).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}

/**
 * Allows for a dialog to be shown as a destination in the navigation graph
 * Uses a custom FloatingWindowNavigator to show the dialog instead of traditional DialogNavigator
 * that wraps the content in a dialog.
 *
 * This allows for dialogs to be defined using the compose `Dialog` but still be navigated to
 */
fun NavGraphBuilder.dialog(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        FloatingWindowNavigator.Destination(
            provider[FloatingWindowNavigator::class],
            content
        ).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}