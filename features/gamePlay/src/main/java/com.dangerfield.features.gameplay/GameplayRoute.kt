package com.dangerfield.features.gameplay

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

// TODO cleanup seperate into different fiels
fun Router.navigateToGamePlayScreen(
    accessCode: String,
    timeLimit: Int? = null
) {
    navigate(
        fillRoute(gamePlayScreenRoute) {
            fill(accessCodeArgument, accessCode)
            fill(timeLimitArgument, timeLimit)
            popUpTo(welcomeNavigationRoute)
        }
    )
}

fun Router.navigateToSingleDeviceInfoRoute(
    accessCode: String,
    timeLimit: Int? = null
) {
    navigate(
        fillRoute(singleDeviceInfoRoute) {
            fill(accessCodeArgument, accessCode)
            fill(timeLimitArgument, timeLimit)
            popUpTo(welcomeNavigationRoute)
        }
    )
}

fun Router.navigateToSingleDeviceGamePlayScreen(
    accessCode: String,
    timeLimit: Int? = null
) {
    navigate(
        fillRoute(singleDeviceGamePlayRoute) {
            fill(accessCodeArgument, accessCode)
            fill(timeLimitArgument, timeLimit)
            popUpTo(singleDeviceInfoRoute)
        }
    )
}

val accessCodeArgument = navArgument("accessCode") { type = NavType.StringType }

val timeLimitArgument = navArgument("timeLimit") {
    type = NavType.IntType
    defaultValue = 0
}

val gamePlayScreenRoute = route("gamePlay") {
    argument(accessCodeArgument)
    argument(timeLimitArgument)
}

val singleDeviceInfoRoute = route("singleDeviceInfo") {
    argument(accessCodeArgument)
    argument(timeLimitArgument)
}

val singleDeviceGamePlayRoute = route("singleDeviceGamePlay") {
    argument(accessCodeArgument)
    argument(timeLimitArgument)
}

