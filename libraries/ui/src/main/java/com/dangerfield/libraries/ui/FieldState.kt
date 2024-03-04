package com.dangerfield.libraries.ui

sealed class FieldState<out T>(val value: T?) {
    data class Idle<T>(val input: T, val message: String? = null) : FieldState<T>(input)
    data class Valid<T>(val input: T, val message: String? = null) : FieldState<T>(input)
    data class Invalid<T>(val input: T?, val errorMessage: String) : FieldState<T>(input)
    data class Error<T>(val input: T? = null, val errorMessage: String? = null) :
        FieldState<T>(input)
}