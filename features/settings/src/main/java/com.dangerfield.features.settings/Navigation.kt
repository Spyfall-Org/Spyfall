package com.dangerfield.features.settings

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(settingsNavigationRoute, navOptions)
}

const val settingsNavigationRoute = "settings"

