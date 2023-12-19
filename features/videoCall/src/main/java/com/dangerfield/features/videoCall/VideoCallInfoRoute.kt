package com.dangerfield.features.videoCall

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToVideoCallLinkInfo() {
    navigate(videoCallLinkInfoRoute.noArgRoute())
}

val videoCallLinkInfoRoute = route("videoCallLinkInfo")
