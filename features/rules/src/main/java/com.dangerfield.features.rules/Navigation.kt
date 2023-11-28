package com.dangerfield.features.rules

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val rulesNavigationRoute = "rulesNavigationRoute"

fun NavController.navigateToRules(navOptions: NavOptions? = null) {
    this.navigate(rulesNavigationRoute, navOptions)
}