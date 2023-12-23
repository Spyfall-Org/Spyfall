package com.dangerfield.features.settings.internal

import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route

fun Router.navigateToAbout() {
    navigate(aboutRoute.noArgRoute())
}

fun Router.navigateToContactUs() {
    navigate(contactUsRoute.noArgRoute())
}

val aboutRoute = route("about")

val contactUsRoute = route("contactUs")