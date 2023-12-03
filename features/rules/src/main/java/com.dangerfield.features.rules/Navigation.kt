package com.dangerfield.features.rules

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val rulesNavigationRoute = route("rulesNavigationRoute")

fun Router.navigateToRules() {
    navigate(rulesNavigationRoute.noArgRoute())
}