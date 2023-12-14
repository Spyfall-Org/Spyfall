package com.dangerfield.libraries.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import spyfallx.core.Try
import spyfallx.core.checkInDebug
import spyfallx.core.developerSnackOnError
import spyfallx.core.logOnError

inline fun <reified T : Any> NavBackStackEntry.navArgument(navArgument: NamedNavArgument): T? = Try {

    val argValue = when (navArgument.argument.type) {
        NavType.BoolType -> arguments?.getBoolean(navArgument.name)
        NavType.IntType -> arguments?.getInt(navArgument.name)
        NavType.FloatType -> arguments?.getFloat(navArgument.name)
        NavType.LongType -> arguments?.getLong(navArgument.name)
        NavType.StringType -> arguments?.getString(navArgument.name)
        is NavType.SerializableType -> arguments?.getSerializable(navArgument.name)
        else -> null
    } as? T?

    val value = argValue
        ?: savedStateHandle.navArgument(navArgument, checkExists = false)
        ?: navArgument.argument.defaultValue as? T?

    checkInDebug(
        value != null
                || navArgument.argument.isNullable
                || navArgument.argument.defaultValue != null
    ) {
        "Argument ${navArgument.name} is not nullable/default-able but was not found in NavBackStackEntry"
    }

    value
}
    .developerSnackOnError { "NavBackStackEntry did not have expected arg: ${navArgument.name}" }
    .logOnError()
    .getOrNull()