package com.dangerfield.features.waitingroom

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val waitingRoomNavigationRoute = "waitingRoom"

fun NavController.navigateToWaitingRoom(navOptions: NavOptions? = null) {
    navigate(waitingRoomNavigationRoute, navOptions)
}