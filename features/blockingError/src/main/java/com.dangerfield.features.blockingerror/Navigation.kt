package com.dangerfield.features.blockingerror

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val blockingErrorRoute = route("blocking_error")

val maintenanceRoute = route("maintenance")

fun Router.navigateToBlockingError() {
    this.navigate(blockingErrorRoute.noArgRoute())
}
