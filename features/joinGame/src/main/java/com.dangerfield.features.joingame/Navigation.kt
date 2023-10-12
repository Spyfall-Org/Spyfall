package com.dangerfield.features.joingame

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val joinGameNavigationRoute = "joinGame"

fun NavController.navigateToJoinGame(navOptions: NavOptions? = null) {
    navigate(joinGameNavigationRoute, navOptions)
}