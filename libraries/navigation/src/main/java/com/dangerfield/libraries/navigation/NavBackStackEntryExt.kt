package com.dangerfield.libraries.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import oddoneout.core.Catching
import oddoneout.core.checkInDebug
import oddoneout.core.debugSnackOnError
import oddoneout.core.logOnFailure

/**
 * Gets a nav argument from a NavBackStackEntry. Prioritizes pulling from savedStateHandle,
 * falls back to arguments
 *
 * @param navArgument the argument to get
 *
 *
 */
inline fun <reified T : Any> NavBackStackEntry.navArgument(navArgument: NamedNavArgument): T? =
    Catching {
        val argValue = when (navArgument.argument.type) {
            NavType.BoolType -> arguments?.getBoolean(navArgument.name)
            NavType.IntType -> arguments?.getInt(navArgument.name)
            NavType.FloatType -> arguments?.getFloat(navArgument.name)
            NavType.LongType -> arguments?.getLong(navArgument.name)
            NavType.StringType -> arguments?.getString(navArgument.name)
            is NavType.SerializableType -> arguments?.getSerializable(navArgument.name)
            else -> null
        } as? T?

        // prioritize getting the value from the saved state handle
        val value = savedStateHandle.navArgument(navArgument, checkExists = false)
                ?: argValue
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
        .debugSnackOnError { "NavBackStackEntry did not have expected arg: ${navArgument.name}" }
        .logOnFailure()
        .getOrNull()