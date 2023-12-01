package com.dangerfield.libraries.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import spyfallx.core.Try
import spyfallx.core.checkInDebug
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug

fun <T : Any> SavedStateHandle.navArgument(navArgument: NamedNavArgument): T? = Try {
    val value = get<T>(navArgument.name)
    checkInDebug(value != null
            || navArgument.argument.isNullable
            || navArgument.argument.defaultValue != null
    ) {
        "Argument ${navArgument.name} is not nullable/default-able but was not found in SavedStateHandle"
    }

    value
}
    .logOnError()
    .throwIfDebug()
    .getOrNull()