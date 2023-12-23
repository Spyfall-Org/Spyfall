package com.dangerfield.features.settings.internal

sealed class FieldState<out T>(val backingValue: T?) {
    data class Idle<T>(val value: T) : FieldState<T>(value)
    data class Valid<T>(val value: T) : FieldState<T>(value)
    data class Invalid<T>(val value: T?, val errorMessage: String) : FieldState<T>(value)
    data class Error<T>(val value: T? = null, val errorMessage: String? = null) :
        FieldState<T>(value)

}