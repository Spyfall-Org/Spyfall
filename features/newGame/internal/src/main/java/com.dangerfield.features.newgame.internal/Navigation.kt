package com.dangerfield.features.newgame.internal

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToVideoCallLinkInfo() {
    navigate(videoCallLinkInfoRoute.build())
}

val videoCallLinkInfoRoute = route("videoCallLinkInfo")
