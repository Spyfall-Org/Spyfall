package com.dangerfield.features.joingame

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val joinGameNavigationRoute = route("joinGame")

fun Router.navigateToJoinGame() {
    navigate(joinGameNavigationRoute.noArgRoute())
}
