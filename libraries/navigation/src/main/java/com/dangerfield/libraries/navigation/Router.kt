package com.dangerfield.libraries.navigation

import androidx.navigation.NavBackStackEntry
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState

/**
 * The Router is the interface that the calling app will use to navigate around the app.
 */
interface Router {
    /**
     * The current route name of the app. This is the name of the route that the app is currently on.
     */
    val currentRouteName: String?

    fun navigate(filledRoute: Route.Filled)
    fun goBack()
    fun openWebLink(url: String, openInApp: Boolean = true)
    fun popBackTo(route: Route.Template, inclusive: Boolean = false)
    fun dismissSheet(sheetState: BottomSheetState)
    fun ifStillOn(backStackEntry: NavBackStackEntry, action: Router.() -> Unit)
    fun getBackStackEntry(route: Route.Template): NavBackStackEntry
}