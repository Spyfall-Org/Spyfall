package com.dangerfield.libraries.test

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show
import oddoneout.core.Catching

fun <T> Assert<Catching<T>>.isSuccess(): Assert<T> = transform { actual ->
    actual.fold(
        onFailure = { expected("to be Success but was Failure${show(it)}") },
        onSuccess = { it }
    )
}

fun Assert<Catching<*>>.isFailure(): Assert<Throwable> = transform { actual ->
    actual.fold(
        onFailure = { it },
        onSuccess = { expected("to be Failure but was Success${show(it)}") }
    )
}
