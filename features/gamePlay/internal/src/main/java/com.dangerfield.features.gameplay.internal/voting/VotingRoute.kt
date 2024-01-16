package com.dangerfield.features.gameplay.internal.voting

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.singleDeviceVotingParentRoute
import com.dangerfield.features.gameplay.singleDeviceInfoRoute
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route
// TODO consider sealed class I think
fun Router.navigateToVotingInfo(hasVoted: Boolean = false) {
    navigate(
        fillRoute(votingInfoRoute) {
            fill(hasVotedArgument, hasVoted)
        }
    )
}

fun Router.navigateToSingleDeviceVoting(accessCode: String) {
    navigate(
        fillRoute(singleDeviceVotingParentRoute) {
            fill(accessCodeArgument, accessCode)
            popUpTo(singleDeviceInfoRoute)
        }
    )
}

fun Router.navigateToSingleDeviceVotingResults(accessCode: String) {
    navigate(
        fillRoute(singleDeviceVotingResultsRoute) {
            fill(accessCodeArgument, accessCode)
            popUpTo(singleDeviceVotingRoute, inclusive = true)
        }
    )
}

val hasVotedArgument = navArgument("hasVoted") { type = NavType.BoolType }

val votingInfoRoute = route("voting_info") {
    argument(hasVotedArgument)
}

val singleDeviceVotingRoute = route("single_device_voting") {
    argument(accessCodeArgument)
}

val singleDeviceVotingResultsRoute = route("single_device_results") {
    argument(accessCodeArgument)
}