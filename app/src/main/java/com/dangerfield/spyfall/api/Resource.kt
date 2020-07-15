package com.dangerfield.spyfall.api

import java.lang.Exception

/**
 * Used to feed response from repository layer to view as {Success || Loading || Error}
 * These states can carry any present data with it while letting the view know data fetching status
 */
sealed class Resource<T, E>(
    val data: T? = null,
    val error: E? = null,
    val exception: Exception? = null
) {
    class Success<T,E>(data: T) : Resource<T,E>(data)
    class Error<T,E>(data: T? = null, error: E, exception: Exception? = null) : Resource<T,E>(data, error, exception)
}