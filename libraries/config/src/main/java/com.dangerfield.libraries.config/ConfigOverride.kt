package com.dangerfield.libraries.config

import com.squareup.moshi.JsonClass

/**
 * Used to force the value in the config at the provided path to be the provided value
 */
@JsonClass(generateAdapter = true)
class ConfigOverride<T: Any> (
    val path: String,
    val value: T
)