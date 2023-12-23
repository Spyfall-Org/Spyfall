package com.dangerfield.libraries.navigation.internal

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavHostController
import com.dangerfield.libraries.navigation.Route
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import spyfallx.core.Try
import spyfallx.core.developerSnackOnError
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug

class NavControllerRouter(
    val navHostController: NavHostController,
    private val coroutineScope: CoroutineScope
) : Router {

    init {
        Log.d("Elijah", "New nav controller initialized $this")
    }

    override val currentRouteName: String?
        get() = navHostController.currentDestination?.route

    override fun navigate(filledRoute: Route.Filled) {
        Try {
            navHostController.navigate(filledRoute.route, filledRoute.navOptions())
        }
            .logOnError()
            .throwIfDebug()
    }

    override fun goBack() {
        Try {
            navHostController.popBackStack()
        }
            .logOnError()
            .throwIfDebug()
    }

    override fun openWebLink(url: String) {
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
                navHostController.context.openWebLinkFromContext(it)
            }
            .developerSnackOnError { "Could not open web link $url" }
            .logOnError()
    }


    override fun popBackTo(route: Route.Template, inclusive: Boolean) {
        Try {
            navHostController.popBackStack(route.navRoute, inclusive)
        }
            .logOnError()
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
                .logOnError()
                .throwIfDebug()

        } else {
            goBack()
        }
    }
}