package com.dangerfield.spyfall.api

/**
 * Used to feed response from repository layer to view as {Success || Loading || Error}
 * These states can carry any present data with it while letting the view know data fetching status
 */
sealed class Resource<T, E>(
    val data: T? = null,
    val error: E? = null
) {
    class Success<T,E>(data: T) : Resource<T,E>(data)
    class Loading<T,E>(data: T? = null) : Resource<T,E>(data)
    class Error<T,E>(data: T? = null, error: E) : Resource<T,E>(data, error)
}