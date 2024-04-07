package com.dangerfield.libraries.dictionary

import androidx.annotation.StringRes

/**
 * Abstraction over source of strings for the app.
 */
interface Dictionary {

    /**
     * retrieves a string from the dictionary, throws an error if it doesnt exist
     *
     * @param args map of replacement keys to replacement values
     * a templated string is stored in the format: "Hello {name}"
     *
     * a call to getString(R.string.hello, mapOf("name" to "John")) will return "Hello John"
     */
    fun getString(@StringRes key: Int, args: Map<String,String> = emptyMap()): String

    /**
     * retrieves a string from the dictionary, returns null if it doesnt exist
     *
     * @param args map of replacement keys to replacement values
     * a templated string is stored in the format: "Hello {name}"
     *
     * a call to getOptionalString(R.string.hello, mapOf("name" to "John")) will return "Hello John"
     */
    fun getOptionalString(@StringRes key: Int, args: Map<String,String> = emptyMap()): String?
}

/**
 * Helper function to allow for vararg replacement values
 */
fun Dictionary.getString(@StringRes key: Int, vararg args: Pair<String,String>): String {
    return getString(key, args.toMap())
}


