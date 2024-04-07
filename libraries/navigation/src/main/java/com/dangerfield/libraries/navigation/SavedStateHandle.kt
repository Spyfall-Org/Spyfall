package com.dangerfield.libraries.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import oddoneout.core.Catching
import oddoneout.core.checkInDebug
import oddoneout.core.debugSnackOnError
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug

/**
 * Gets a nav argument from a SavedStateHandle
 *
 * @param navArgument the argument to get
 * @param checkExists whether to check if the argument exists in the SavedStateHandle
 */
fun <T : Any> SavedStateHandle.navArgument(navArgument: NamedNavArgument, checkExists: Boolean = true): T? = Catching {
    val value = get<T>(navArgument.name)
    if (checkExists) {
        // check if the argument exists in the saved state handle. Only runs in debug.
        // Throws error if not found and the arg doesnt have a default or is nullable
        checkInDebug(value != null
                || navArgument.argument.isNullable
                || navArgument.argument.defaultValue != null
        ) {
            "Argument ${navArgument.name} is not nullable/default-able but was not found in SavedStateHandle"
        }
    }
    value
}

    .debugSnackOnError { "Saved state handle did not have expected: ${navArgument.name}" }
    .logOnFailure()
    .getOrNull()


fun SavedStateHandle.updateArg(navArgument: NamedNavArgument, value: Any?) {
    Catching {
        set(navArgument.name, value)
    }
        .logOnFailure()
        .throwIfDebug()
}