package com.dangerfield.features.rules

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val rulesNavigationRoute = route("rules")

fun Router.navigateToRules() {
    navigate(rulesNavigationRoute.noArgRoute())
}