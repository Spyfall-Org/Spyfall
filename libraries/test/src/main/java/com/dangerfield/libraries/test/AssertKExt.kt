package com.dangerfield.libraries.test

import assertk.Assert
import assertk.assertions.isSameInstanceAs
import assertk.assertions.prop
import assertk.assertions.support.expected
import assertk.assertions.support.fail
import assertk.assertions.support.show
import spyfallx.core.Try

fun <T> Assert<Try<T>>.isSuccess(): Assert<T> = transform { actual ->
    actual.fold(
        onFailure = { expected("to be Success but was Failure${show(it)}") },
        onSuccess = { it }
    )
}

/**
 * Asserts the value is Unit using `==`.
 */
fun <T> Assert<T>.isUnit() = given { actual ->
    if (actual == Unit) return
    fail(Unit, actual)
}

fun Assert<Try<*>>.isFailure(): Assert<Throwable> = transform { actual ->
    actual.fold(
        onFailure = { it },
        onSuccess = { expected("to be Failure but was Success${show(it)}") }
    )
}

fun Assert<Try<*>>.isFailure(error: Throwable) {
    isFailure().isSameInstanceAs(error)
}

fun <T> Assert<Pair<T, *>>.first(): Assert<T> = prop(Pair<T, *>::first)
fun <T> Assert<Pair<*, T>>.second(): Assert<T> = prop(Pair<*, T>::second)