package com.dangerfield.features.newgame

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val newGameNavigationRoute = "newGame"

fun NavController.navigateToNewGame(navOptions: NavOptions? = null) {
    navigate(newGameNavigationRoute, navOptions)
}