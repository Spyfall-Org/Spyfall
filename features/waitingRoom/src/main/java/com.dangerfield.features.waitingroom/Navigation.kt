package com.dangerfield.features.waitingroom

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.navigateToWaitingRoom(navOptions: NavOptions? = null) {
    navigate(waitingRoomNavigationRoute, navOptions)
}

const val waitingRoomNavigationRoute = "waitingRoom"
