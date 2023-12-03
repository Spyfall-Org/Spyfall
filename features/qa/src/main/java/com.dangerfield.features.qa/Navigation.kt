package com.dangerfield.features.qa

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val qaNavigationRoute = route("qaNavigationRoute")

fun Router.navigateToQa() {
    navigate(qaNavigationRoute.noArgRoute())
}
