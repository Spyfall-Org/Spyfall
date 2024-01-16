package com.dangerfield.features.gameplay.internal.help

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToGameHelp() {
    navigate(
        gameHelpRoute.noArgRoute()
    )
}

val gameHelpRoute = route("in_game_help_bottom_sheet")
