package com.dangerfield.features.example

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToExample() {
    navigate(
        fillRoute(exampleRoute) {
            //fill(someArgument, someValue)
        }
    )
}

val exampleRoute = route("example") {
    // argument(someArgument)
}