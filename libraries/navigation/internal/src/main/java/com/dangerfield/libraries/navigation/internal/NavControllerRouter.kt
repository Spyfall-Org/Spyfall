package com.dangerfield.libraries.navigation.internal

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.dangerfield.libraries.navigation.Route
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug

class NavControllerRouter(
    private val navController: NavController,
    private val coroutineScope: CoroutineScope
) : Router {
    override fun navigate(filledRoute: Route.Filled) {
        Try {
            navController.navigate(filledRoute.route, filledRoute.navOptions())
        }
            .logOnError()
            .throwIfDebug()
    }

    override fun goBack() {
        Try {
            navController.popBackStack()
        }
            .logOnError()
            .throwIfDebug()
    }

    override fun popBackTo(route: Route.Template, inclusive: Boolean) {
        Try {
            navController.popBackStack(route.navRoute, inclusive)
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