package com.dangerfield.libraries.navigation

import androidx.navigation.NavBackStackEntry
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState


interface Router {
    val currentRouteName: String?
    fun navigate(filledRoute: Route.Filled)
    fun goBack()
    fun openWebLink(url: String)
    fun popBackTo(route: Route.Template, inclusive: Boolean = false)
    fun dismissSheet(sheetState: BottomSheetState)
    fun ifStillOn(backStackEntry: NavBackStackEntry, action: Router.() -> Unit)
    fun getBackStackEntry(route: Route.Template): NavBackStackEntry
}