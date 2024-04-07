package com.dangerfield.libraries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug

/**
 * Retrieves a view model scoped to the route nav backstack entry.
 * This is useful for scoping a view model to a nested graph to be shared between multiple destinations
 */
@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.viewModelScopedTo(
    route: Route.Template,
    router: Router
): VM {

    val fallbackScope = remember {
        Catching { destination.parent!!.route!! }
            .logOnFailure("Could not get parent route to scope to from ${this.destination.route}")
            .throwIfDebug()
            .getOrElse { this.destination.route ?: "" }
            .let {
                route(it)
            }
    }

    val backstackEntryScope = remember {
        Catching { router.getBackStackEntry(route) }
            .throwIfDebug()
            .getOrElse { router.getBackStackEntry(fallbackScope) }
    }

    return hiltViewModel(backstackEntryScope)
}