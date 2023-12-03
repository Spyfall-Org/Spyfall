package com.dangerfield.features.welcome

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val welcomeNavigationRoute = route("welcome")

fun Router.navigateToWelcome() {
    this.navigate(welcomeNavigationRoute.noArgRoute())
}
