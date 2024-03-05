package com.dangerfield.features.qa

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val qaNavigationRoute = route("qa")

fun Router.navigateToQa() {
    navigate(qaNavigationRoute.noArgRoute())
}
