package com.dangerfield.features.forcedupdate

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

val forcedUpdateNavigationRoute = route("forced_update")

fun Router.navigateToForcedUpdate() {
    this.navigate(forcedUpdateNavigationRoute.noArgRoute())
}
