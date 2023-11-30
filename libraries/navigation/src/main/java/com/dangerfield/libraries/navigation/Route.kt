package com.dangerfield.libraries.navigation

import androidx.navigation.NamedNavArgument

data class Route(
    internal val rawRoute: String
)

fun Route.withArgument(argument: NamedNavArgument) = Route(
    if (rawRoute.contains("?")) {
        "$rawRoute&${argument.name}={${argument.name}}"
    } else {
        "$rawRoute?${argument.name}={${argument.name}}"
    }
)

fun Route.withArguments(vararg arguments: NamedNavArgument) =
    arguments.fold(this) { acc, argument ->
        acc.withArgument(argument)
    }

private fun Route.hasArgument(argument: NamedNavArgument) =
    rawRoute.contains("${argument.name}={${argument.name}}")

@Suppress("UnusedPrivateMember")
private fun Route.hasAnyArguments() = rawRoute.contains("?")
private fun Route.hasOnlyOneArgument() = rawRoute.contains("?") && !rawRoute.contains("&")
private fun Route.hasMoreThanOneArgument() = rawRoute.contains("?") && rawRoute.contains("&")
private fun Route.hasFirstArgument(argument: NamedNavArgument) =
    rawRoute.contains("?${argument.name}={${argument.name}}")

fun Route.build(argument: NamedNavArgument, value: Any?) = Route(
    when {
        value != null -> rawRoute.replace("{${argument.name}}", value.toString())
        !hasArgument(argument) -> rawRoute

        hasOnlyOneArgument() -> rawRoute.replace("?${argument.name}={${argument.name}}", "")

        hasFirstArgument(argument) && this.hasMoreThanOneArgument() -> {
            rawRoute.replace("?${argument.name}={${argument.name}}&", "?")
        }

        else -> rawRoute.replace("&${argument.name}={${argument.name}}", "")
    }
).build()

fun Route.build(vararg args: Pair<NamedNavArgument, Any?>): String {

    val builtRoute = args.fold(rawRoute) { acc, pair ->
        Route(acc).build(pair.first, pair.second)
    }

    return Route(builtRoute).build()
}

fun Route.build() = this.rawRoute

fun Route.rawRoute() = this.rawRoute
