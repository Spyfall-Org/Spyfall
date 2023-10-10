package com.dangerfield.features.welcome

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val welcomeNavigationRoute = "welcome"

fun NavController.navigateToForcedUpdate(navOptions: NavOptions? = null) {
    this.navigate(welcomeNavigationRoute, navOptions)
}