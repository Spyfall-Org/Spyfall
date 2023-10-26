package com.dangerfield.features.forcedupdate

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val forcedUpdateNavigationRoute = "forced_update"

fun NavController.navigateToForcedUpdate(navOptions: NavOptions? = null) {
    this.navigate(forcedUpdateNavigationRoute, navOptions)
}
