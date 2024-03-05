package com.dangerfield.libraries.test

import assertk.Assert
import assertk.assertions.isSameInstanceAs
import assertk.assertions.prop
import assertk.assertions.support.expected
import assertk.assertions.support.fail
import assertk.assertions.support.show
import oddoneout.core.Try

fun <T> Assert<Try<T>>.isSuccess(): Assert<T> = transform { actual ->
    actual.fold(
        onFailure = { expected("to be Success but was Failure${show(it)}") },
        onSuccess = { it }
    )
}

fun Assert<Try<*>>.isFailure(): Assert<Throwable> = transform { actual ->
    actual.fold(
        onFailure = { it },
        onSuccess = { expected("to be Failure but was Success${show(it)}") }
    )
}
