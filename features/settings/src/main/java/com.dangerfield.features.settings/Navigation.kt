package com.dangerfield.features.settings

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToSettings() {
    navigate(settingsNavigationRoute.build())
}

val settingsNavigationRoute = route("settings")

