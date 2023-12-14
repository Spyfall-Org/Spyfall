package com.dangerfield.features.waitingroom

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToWaitingRoom(
    accessCode: String,
) {
    navigate(
        fillRoute(waitingRoomRoute) {
            fill(accessCodeArgument, accessCode)
            popUpTo(welcomeNavigationRoute)
        }
    )
}

val accessCodeArgument = navArgument("accessCode") { type = NavType.StringType }

val waitingRoomRoute = route("waitingRoom") {
    argument(accessCodeArgument)
}
