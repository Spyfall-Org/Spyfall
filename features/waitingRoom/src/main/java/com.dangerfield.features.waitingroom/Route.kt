package com.dangerfield.features.waitingroom

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToWaitingRoom(
    accessCode: String,
    videoCallLink: String? = null
) {
    navigate(
        waitingRoomRoute
            .fill(accessCodeArgument, accessCode)
            .fill(videoCallLinkArgument, videoCallLink)
            .popUpTo(welcomeNavigationRoute)
            .build(),
    )
}

val accessCodeArgument = navArgument("accessCode") { type = NavType.StringType }

val videoCallLinkArgument = navArgument("videoCallLink") {
    type = NavType.StringType
    nullable = true
}

val waitingRoomRoute = route("waitingRoom") {
    argument(accessCodeArgument)
    argument(videoCallLinkArgument)
}
