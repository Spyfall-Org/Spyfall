package com.dangerfield.features.joingame

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val joinGameNavigationRoute = route("join_game")

fun Router.navigateToJoinGame() {
    navigate(joinGameNavigationRoute.noArgRoute())
}
