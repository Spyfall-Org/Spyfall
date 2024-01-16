package com.dangerfield.features.videoCall

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToVideoCallBottomSheet(link: String) {
    navigate(
        fillRoute(videoCallDetails) {
            fill(videoLinkArgument, link)
        }
    )
}

val videoLinkArgument = navArgument("videoLink") { type = NavType.StringType }

val videoCallDetails = route("video_call_details") {
    argument(videoLinkArgument)
}
