package com.dangerfield.libraries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import oddoneout.core.Try
import oddoneout.core.getOrElse
import oddoneout.core.logOnError
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
        Try { destination.parent!!.route!! }
            .logOnError("Could not get parent route to scope to from ${this.destination.route}")
            .throwIfDebug()
            .getOrElse { this.destination.route ?: "" }
            .let {
                route(it)
            }
    }

    val backstackEntryScope = remember {
        Try { router.getBackStackEntry(route) }
            .throwIfDebug()
            .getOrElse { router.getBackStackEntry(fallbackScope) }
    }

    return hiltViewModel(backstackEntryScope)
}