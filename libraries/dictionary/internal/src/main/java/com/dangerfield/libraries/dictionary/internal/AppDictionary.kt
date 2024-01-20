package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.applyArgs
import oddoneout.core.BuildInfo
import oddoneout.core.Try

/**
 * Main dictionary to be used for the app that resolves values between the
 * default and override dictionaries
 */
class AppDictionary(
    private val defaultDictionary: Dictionary,
    private val overrideDictionary: Dictionary?,
    private val buildInfo: BuildInfo,
    private val context: Context
) : Dictionary {

    /**
     * Gets a string from the override dictionary if it exists, otherwise
     * gets it from the default dictionary. If it doesn't exist in either,
     * returns the resource name as a string in debug and empty string otherwise
     */
    override fun getString(key: Int, args: Map<String, String>): String {
        val value = Try { overrideDictionary?.getString(key, args) }.getOrNull()
            ?: Try { defaultDictionary.getString(key, args) }.getOrNull()
            ?: context.resources.getResourceEntryName(key).takeIf { buildInfo.isDebug }
            ?: ""
        return value.applyArgs(args)
    }

    override fun getOptionalString(key: Int, args: Map<String, String>): String? {
        val value = Try { overrideDictionary?.getString(key, args) }.getOrNull()
            ?: Try { defaultDictionary.getString(key, args) }.getOrNull()
        return value?.applyArgs(args)
    }

}