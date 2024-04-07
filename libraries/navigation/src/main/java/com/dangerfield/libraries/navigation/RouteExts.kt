@file:Suppress("UnusedFunction")
package com.dangerfield.libraries.navigation

import androidx.navigation.NamedNavArgument

// NOTE: ALL EXTENSIONS IN THIS FILE ARE USED INTERNALLY BY THE NAVIGATION LIBRARY
// AND ARE NOT INTENDED FOR USE OUTSIDE OF THE LIBRARY

/**
 * @return  true if the route contains any arguments
 * operates under the assumption that all routes are build with the RouteBuilder
 * which uses "?arg{arg}&arg2={arg2}" syntax
 */
internal fun StringBuilder.hasAnyArguments() = contains("?")

/**
 * @return true if the route contains only 1 argument
 * operates under the assumption that all routes are build with the RouteBuilder
 * which uses "?arg{arg}&arg2={arg2}" syntax
 */
internal fun StringBuilder.hasOnlyOneArgument() = contains("?") && !contains("&")

/**
 * @return true if the route contains more than 1 argument
 * operates under the assumption that all routes are build with the RouteBuilder
 * which uses "?arg{arg}&arg2={arg2}" syntax
 */
internal fun StringBuilder.hasMoreThanOneArgument() = contains("?") && contains("&")

/**
 * @return true if the route contains an argument with the given name
 */
internal fun StringBuilder.hasArgument(argument: NamedNavArgument) =
    contains("${argument.name}={${argument.name}}")

/**
 * @return true if the provided argument is the first argument in the route
 * operates under the assumption that all routes are build with the RouteBuilder
 * which uses "?arg{arg}&arg2={arg2}" syntax
 */
internal fun StringBuilder.hasFirstArgument(argument: NamedNavArgument) =
    contains("?${argument.name}={${argument.name}}")

/**
 * @return the route with the provided argument filled in with the provided value
 *
 * To support nullable args, if the value is null, the argument will be removed from the route
 */
internal fun StringBuilder.fillArgument(argument: NamedNavArgument, value: Any): StringBuilder =
    replace("{${argument.name}}", value.toString())

/**
 * @return the route with the provided argument removed
 *
 * This is used when null args are provided to the route builder
 */
internal fun StringBuilder.removeArgument(argument: NamedNavArgument): StringBuilder {

    return when {
        !hasArgument(argument) -> this

        // route contains this argument and only this argument, clean removal
        hasOnlyOneArgument() -> replace("?${argument.name}={${argument.name}}", "")

        // route contains multiple arguments, and this one is the first, remove, and replace with ?
        hasFirstArgument(argument) && hasMoreThanOneArgument() -> {
            replace("?${argument.name}={${argument.name}}&", "?")
        }

        // argument is not the first, remove clean
        else -> replace("&${argument.name}={${argument.name}}", "")
    }
}

/**
 * @return true if the provided argument is not filled in the route
 */
internal fun StringBuilder.isArgumentNotFilled(argument: NamedNavArgument) =
    contains("${argument.name}={${argument.name}}")

/**
 * @return true if the provided argument is filled in the route
 */
internal fun StringBuilder.replace(oldValue: String, newValue: String): StringBuilder {
    val index = indexOf(oldValue)
    return replace(index, index + oldValue.length, newValue)
}

internal fun StringBuilder.fillArguments(vararg args: Pair<NamedNavArgument, Any>): String {
    val builtRoute = args.fold(this) { acc, pair ->
        acc.fillArgument(pair.first, pair.second)
    }

    return builtRoute.toString()
}
