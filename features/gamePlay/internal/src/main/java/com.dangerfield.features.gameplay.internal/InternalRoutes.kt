package com.dangerfield.features.gameplay.internal

import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.timeLimitArgument
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

// TODO cleanup seperate into different files, maybe put into a sealed class instead to keep everthing in one place while keeping it organized
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

val singleDevicePlayerRoleRoute = route("single_device_role_reveal") {
    argument(accessCodeArgument)
    argument(timeLimitArgument)
}

val singleDeviceVotingParentRoute = route("single_device_voting_parent") {
    argument(accessCodeArgument)
}