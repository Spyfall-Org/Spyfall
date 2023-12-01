package com.dangerfield.features.blockingerror

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val blockingErrorRoute = route("blockingError")

fun Router.navigateToBlockingError() {
    this.navigate(blockingErrorRoute.build())
}
