package com.dangerfield.features.termOfService

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToTermOfService() {
    navigate(
        fillRoute(termOfServiceRoute) {
            //fill(someArgument, someValue)
        }
    )
}

val termOfServiceRoute = route("termOfService") {
    // argument(someArgument)
}