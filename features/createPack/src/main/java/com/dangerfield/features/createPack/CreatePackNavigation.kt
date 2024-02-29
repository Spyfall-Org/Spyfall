package com.dangerfield.features.createPack

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

fun Router.navigateToCreatePack() {
    navigate(
        fillRoute(createPackRoute) {
            //fill(someArgument, someValue)
        }
    )
}

val createPackRoute = route("createPack") {
    // argument(someArgument)
}