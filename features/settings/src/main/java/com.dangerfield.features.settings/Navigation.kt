package com.dangerfield.features.settings

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToSettings() {
    navigate(settingsNavigationRoute.noArgRoute())
}

val settingsNavigationRoute = route("settings")

