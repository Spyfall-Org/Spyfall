package com.dangerfield.libraries.navigation.internal

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.dangerfield.libraries.navigation.Route
import com.dangerfield.libraries.navigation.Router

class NavControllerRouter(
    private val navController: NavController
) : Router {
    override fun navigate(filledRoute: Route.Filled) {
        navController.navigate(filledRoute.route, filledRoute.navOptions())
    }

    override fun goBack() {
        navController.popBackStack()
    }
}