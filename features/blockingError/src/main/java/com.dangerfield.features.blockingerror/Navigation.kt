package com.dangerfield.features.blockingerror

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.fillRoute
import com.dangerfield.libraries.navigation.route

val blockingErrorRoute = route("blockingError")

fun Router.navigateToBlockingError() {
    this.navigate(blockingErrorRoute.noArgRoute())
}
