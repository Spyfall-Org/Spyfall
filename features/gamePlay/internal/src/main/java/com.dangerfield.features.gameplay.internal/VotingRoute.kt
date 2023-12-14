package com.dangerfield.features.gameplay.internal

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToVotingInfo() {
    navigate(
        votingRoute.noArgRoute()
    )
}

val votingRoute = route("voting")
