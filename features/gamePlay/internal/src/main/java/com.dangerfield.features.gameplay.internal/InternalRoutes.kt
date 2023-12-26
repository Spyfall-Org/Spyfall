package com.dangerfield.features.gameplay.internal

import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.singleDeviceInfoRoute
import com.dangerfield.features.gameplay.timeLimitArgument
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToSingleDevicePlayerRoleRoute(
    accessCode: String,
    timeLimit: Int? = null
) {
    navigate(
        fillRoute(singleDevicePlayerRoleRoute) {
            fill(accessCodeArgument, accessCode)
            fill(timeLimitArgument, timeLimit)
        }
    )
}

val singleDevicePlayerRoleRoute = route("singleDevicePlayerRole") {
    argument(accessCodeArgument)
    argument(timeLimitArgument)
}


val singleDeviceVotingNavigationRoute = route("singleDeviceVotingNavigationRoute") {
    argument(accessCodeArgument)
}