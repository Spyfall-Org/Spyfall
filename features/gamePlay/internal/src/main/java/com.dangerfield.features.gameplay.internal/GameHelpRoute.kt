package com.dangerfield.features.gameplay.internal

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToGameHelp() {
    navigate(
        gameHelpRoute.noArgRoute()
    )
}

val gameHelpRoute = route("gameHelp")
