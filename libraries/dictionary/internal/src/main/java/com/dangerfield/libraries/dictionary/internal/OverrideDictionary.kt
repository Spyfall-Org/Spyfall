package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.applyArgs

/**
 * A dictionary implementation with values that will be preferred over the default if they exist.
 *
 * values are backed by a map of String to String with keys that correspond to the names of the
 * string resources locally
 *
 * ex:
 * string.xml
 * <string name="some_title">Some Default Title</string>
 *
 * map:
 * mapOf("some_title" to "Some Overridden Title")
 */
class OverrideDictionary(
    private val context: Context,
    val map: Map<String, String>
) : Dictionary {

    override fun getString(key: Int, args: Map<String,String>): String {
        val stringKey = context.resources.getResourceEntryName(key)
        return map[stringKey]?.applyArgs(args) ?: throw IllegalArgumentException("No string found for key: $stringKey")
    }

    override fun getOptionalString(key: Int, args: Map<String,String>): String? {
        val stringKey = context.resources.getResourceEntryName(key)
        return map[stringKey]?.applyArgs(args)
    }
}
