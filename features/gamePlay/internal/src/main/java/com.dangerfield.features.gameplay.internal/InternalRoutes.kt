package com.dangerfield.features.gameplay.internal

import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToSingleDevicePlayerRoleRoute(
    accessCode: String,
) {
    navigate(
        fillRoute(singleDevicePlayerRoleRoute) {
            fill(accessCodeArgument, accessCode)
            popUpTo(welcomeNavigationRoute)
        }
    )
}

val singleDevicePlayerRoleRoute = route("singleDevicePlayerRole") {
    argument(accessCodeArgument)
}