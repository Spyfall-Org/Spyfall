package com.dangerfield.libraries.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Used to represent a view that has a state of being idle, valid, invalid, or in an error state.
 * @param T The type of the input value, must be serializable. All fiel
 */
sealed class FieldState<out T>(val value: T?, val error: String? = null): Parcelable {

    /**
     * Default state. Not valid or invalid. Ex: Textfield is empty
     */
    @Parcelize
    data class Idle<T: Serializable>(val input: T?, val message: String? = null) : FieldState<T>(input)

    /**
     * State where the input is valid
     * @param input The input value
     * @param message Optional message to display to the user
     *
     * ex: Email field is valid
     */
    @Parcelize
    data class Valid<T: Serializable>(val input: T?, val message: String? = null) : FieldState<T>(input)

    /**
     * State where the input is invalid
     * @param input The input value
     * @param errorMessage The error message to display to the user
     *
     * ex: Email field is invalid
     */
    @Parcelize
    data class Invalid<T:Serializable>(val input: T?, val errorMessage: String) : FieldState<T>(input, errorMessage)


    /**
     * State where the input is in an error state
     * @param input The input value
     * @param errorMessage The error message to display to the user
     *
     * ex: Email field is in an error state. Maybe a login failed and the email was the problem.
     */
    @Parcelize
    data class Error<T: Serializable>(val input: T? = null, val errorMessage: String? = null) :
        FieldState<T>(input, errorMessage)
}