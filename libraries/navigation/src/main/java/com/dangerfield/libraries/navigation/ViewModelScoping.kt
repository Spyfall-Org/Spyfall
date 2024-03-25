package com.dangerfield.libraries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug

@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.viewModelScopedTo(
    route: Route.Template,
    router: Router
): VM {

    // TODO take the time to understand why rmember worked
    // pretty sure when popping from a nested graph the parent gets killed before the kids
    // which means when the kids tried to use the parents there was a problem.
    // but remember worked
    // fall back to the parent route or the current route
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