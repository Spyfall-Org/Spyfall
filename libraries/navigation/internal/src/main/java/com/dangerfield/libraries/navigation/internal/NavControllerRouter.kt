package com.dangerfield.libraries.navigation.internal

import android.net.Uri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.dangerfield.libraries.navigation.Route
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import oddoneout.core.Try
import oddoneout.core.developerSnackOnError
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug
import timber.log.Timber

class NavControllerRouter(
    val navHostController: NavHostController,
    private val coroutineScope: CoroutineScope
) : Router {

    override val currentRouteName: String?
        get() = navHostController.currentDestination?.route

    init {
        navHostController
            .currentBackStackEntryFlow
            .onEach {
                Timber.d("backstack: ${it.destination.route}")
            }
            .launchIn(coroutineScope)
    }

    override fun navigate(filledRoute: Route.Filled) {
        Try {
            navHostController.navigate(filledRoute.route, filledRoute.navOptions())
        }
            .logOnFailure()
            .throwIfDebug()
    }

    override fun goBack() {
        Try {
            navHostController.popBackStack()
        }
            .logOnFailure()
            .throwIfDebug()
    }

    override fun openWebLink(url: String, openInApp: Boolean) {
        Try { Uri.parse(url) }
            .map { uri ->
                // add https if no scheme
                if (uri?.scheme.isNullOrEmpty()) {
                    uri.buildUpon().scheme("https").build()
                } else {
                    uri
                }
            }
            .map {
                if (openInApp) {
                    navHostController.context.openWebLinkFromContext(it)
                } else {
                    navHostController.context.openWebLinkExternally(it)
                }
            }
            .developerSnackOnError { "Could not open web link $url" }
            .logOnFailure()
    }

    override fun popBackTo(
        route: Route.Template,
        inclusive: Boolean
    ) {
        Try {
            navHostController.popBackStack(route.navRoute, inclusive)
        }
            .logOnFailure()
            .throwIfDebug()

    }

    override fun dismissSheet(sheetState: BottomSheetState) {
        if (sheetState.isVisible) {
            Try {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    goBack()
                }
            }
                .logOnFailure()
                .throwIfDebug()

        } else {
            goBack()
        }
    }

    override fun ifStillOn(backStackEntry: NavBackStackEntry, action: Router.() -> Unit) {
        if (currentRouteName == backStackEntry.destination.route) {
            action.invoke(this)
        }
    }

    override fun getBackStackEntry(route: Route.Template): NavBackStackEntry {
        return navHostController.getBackStackEntry(route.navRoute)
    }
}