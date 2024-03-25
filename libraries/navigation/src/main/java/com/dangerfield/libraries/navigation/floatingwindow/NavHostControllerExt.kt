package com.dangerfield.libraries.navigation.floatingwindow

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.get
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug

fun NavHostController.getFloatingWindowNavigator(): FloatingWindowNavigator? = Catching {
    navigatorProvider.get<Navigator<out NavDestination>>(
        FloatingWindowNavigator.NAME
    ) as? FloatingWindowNavigator ?: throw IllegalStateException("No floating window navigator")
}
    .logOnFailure("No floating window navigator found")
    .throwIfDebug()
    .getOrNull()
