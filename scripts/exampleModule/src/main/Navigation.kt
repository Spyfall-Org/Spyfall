package com.dangerfield.features.example

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val exampleNavigationRoute = "exampleNavigationRoute"

fun NavController.navigateToExample(navOptions: NavOptions? = null) {
    this.navigate(exampleNavigationRoute, navOptions)
}
