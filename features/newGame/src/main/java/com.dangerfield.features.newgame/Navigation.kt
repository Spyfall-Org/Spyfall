package com.dangerfield.features.newgame

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

val newGameNavigationRoute = route("newGame")

//TODO make every screen launch single ton
fun Router.navigateToNewGame() {
    navigate(
        fillRoute(newGameNavigationRoute) {
            launchSingleTop()
        }
    )
}
