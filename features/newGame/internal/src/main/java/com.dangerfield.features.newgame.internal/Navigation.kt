package com.dangerfield.features.newgame.internal

import androidx.navigation.NavController
import com.dangerfield.libraries.navigation.Route
import com.dangerfield.libraries.navigation.build

fun NavController.navigateToVideoCallLinkInfo() {
    navigate(
        videoCallLinkInfoRoute.build()
    ) {
        launchSingleTop = true
    }
}

val videoCallLinkInfoRoute = Route("videoCallLinkInfo")
