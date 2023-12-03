package com.dangerfield.features.newgame.internal

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToVideoCallLinkInfo() {
    navigate(videoCallLinkInfoRoute.noArgRoute())
}

val videoCallLinkInfoRoute = route("videoCallLinkInfo")
