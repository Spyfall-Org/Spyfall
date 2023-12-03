package com.dangerfield.features.newgame

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val newGameNavigationRoute = route("newGame")

fun Router.navigateToNewGame() {
    navigate(newGameNavigationRoute.noArgRoute())
}
