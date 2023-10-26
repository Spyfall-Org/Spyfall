package com.dangerfield.features.newgame

import androidx.navigation.NavController
import androidx.navigation.NavOptions


fun NavController.navigateToNewGame(navOptions: NavOptions? = null) {
    navigate(newGameNavigationRoute, navOptions)
}

const val newGameNavigationRoute = "newGame"

