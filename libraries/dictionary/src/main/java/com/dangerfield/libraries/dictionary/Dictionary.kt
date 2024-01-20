package com.dangerfield.libraries.dictionary

import androidx.annotation.StringRes

/**
 * Abstraction over source of strings for the app.
 */
interface Dictionary {

    /**
     * @param args map of replacement keys to replacement values
     * a templated string is stored in the format: "Hello {name}"
     *
     * a call to getString(R.string.hello, mapOf("name" to "John")) will return "Hello John"
     */
    fun getString(@StringRes key: Int, args: Map<String,String> = emptyMap()): String

    fun getOptionalString(@StringRes key: Int, args: Map<String,String> = emptyMap()): String?
}
