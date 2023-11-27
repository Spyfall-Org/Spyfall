package com.dangerfield.features.qa

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val qaNavigationRoute = "qaNavigationRoute"

fun NavController.navigateToQa(navOptions: NavOptions? = null) {
    this.navigate(qaNavigationRoute, navOptions)
}
