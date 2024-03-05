package com.dangerfield.features.inAppMessaging

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToInAppMessaging() {
    navigate(
        fillRoute(inAppMessagingRoute) {
            //fill(someArgument, someValue)
        }
    )
}

val inAppMessagingRoute = route("inAppMessaging") {
    // argument(someArgument)
}