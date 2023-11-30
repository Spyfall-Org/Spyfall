package com.dangerfield.features.waitingroom

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.libraries.navigation.Route
import com.dangerfield.libraries.navigation.build
import com.dangerfield.libraries.navigation.withArgument
import com.dangerfield.libraries.navigation.withArguments

fun NavController.navigateToWaitingRoom(
    accessCode: String,
    videoCallLink: String? = null
) {
    navigate(
        waitingRoomRoute.build(
            accessCodeArgument to accessCode,
            videoCallLinkArgument to videoCallLink
        )
    ) {
        launchSingleTop = true
    }
}

val accessCodeArgument = navArgument("accessCode") { type = NavType.StringType }

val videoCallLinkArgument = navArgument("videoCallLink") {
    type = NavType.StringType
    nullable = true
}

val waitingRoomRoute = Route("waitingRoom")
    .withArguments(accessCodeArgument, videoCallLinkArgument)
