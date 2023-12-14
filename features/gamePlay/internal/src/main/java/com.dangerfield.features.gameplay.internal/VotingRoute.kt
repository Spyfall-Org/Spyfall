package com.dangerfield.features.gameplay.internal

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToVotingInfo(hasVoted: Boolean = false) {
    navigate(
        fillRoute(votingRoute) {
            fill(hasVotedArgument, hasVoted)
        }
    )
}

val hasVotedArgument = navArgument("hasVoted") { type = NavType.BoolType }

val votingRoute = route("voting") {
    argument(hasVotedArgument)
}
