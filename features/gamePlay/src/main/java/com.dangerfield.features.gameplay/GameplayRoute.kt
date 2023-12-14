package com.dangerfield.features.gameplay

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToGamePlayScreen(
    accessCode: String,
    timeLimit: Int
) {
    navigate(
        fillRoute(gamePlayScreenRoute) {
            fill(accessCodeArgument, accessCode)
            fill(timeLimitArgument, timeLimit)
            popUpTo(welcomeNavigationRoute)
        }
    )
}

val accessCodeArgument = navArgument("accessCode") { type = NavType.StringType }
val timeLimitArgument = navArgument("timeLimit") { type = NavType.IntType }

val gamePlayScreenRoute = route("gamePlay") {
    argument(accessCodeArgument)
    argument(timeLimitArgument)
}
