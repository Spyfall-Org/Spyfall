package com.dangerfield.libraries.navigation.floatingwindow

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.get
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug

fun NavHostController.getFloatingWindowNavigator(): FloatingWindowNavigator? = Try {
    navigatorProvider.get<Navigator<out NavDestination>>(
        FloatingWindowNavigator.NAME
    ) as? FloatingWindowNavigator ?: throw IllegalStateException("No floating window navigator")
}
    .logOnError("No floating window navigator found")
    .throwIfDebug()
    .getOrNull()
