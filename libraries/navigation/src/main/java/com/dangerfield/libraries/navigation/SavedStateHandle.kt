package com.dangerfield.libraries.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import oddoneout.core.Try
import oddoneout.core.checkInDebug
import oddoneout.core.developerSnackOnError
import oddoneout.core.logOnError
import oddoneout.core.throwIfDebug

fun <T : Any> SavedStateHandle.navArgument(navArgument: NamedNavArgument, checkExists: Boolean = true): T? = Try {
    val value = get<T>(navArgument.name)
    if (checkExists) {
        checkInDebug(value != null
                || navArgument.argument.isNullable
                || navArgument.argument.defaultValue != null
        ) {
            "Argument ${navArgument.name} is not nullable/default-able but was not found in SavedStateHandle"
        }
    }
    value
}

    .developerSnackOnError { "Saved state handle did not have expected: ${navArgument.name}" }
    .logOnError()
    .getOrNull()


fun SavedStateHandle.updateArg(navArgument: NamedNavArgument, value: Any?) {
    Try {
        set(navArgument.name, value)
    }
        .logOnError()
        .throwIfDebug()
}