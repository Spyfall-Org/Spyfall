package com.dangerfield.libraries.navigation.floatingwindow

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.get
import oddoneout.core.Try
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug

fun NavHostController.getFloatingWindowNavigator(): FloatingWindowNavigator? = Try {
    navigatorProvider.get<Navigator<out NavDestination>>(
        FloatingWindowNavigator.NAME
    ) as? FloatingWindowNavigator ?: throw IllegalStateException("No floating window navigator")
}
    .logOnFailure("No floating window navigator found")
    .throwIfDebug()
    .getOrNull()
