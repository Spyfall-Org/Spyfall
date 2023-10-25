package com.dangerfield.features.settings

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val settingsNavigationRoute = "settings"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(settingsNavigationRoute, navOptions)
}