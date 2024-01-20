package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.applyArgs
import dagger.hilt.android.qualifiers.ApplicationContext
import oddoneout.core.Try
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A dictionary implementation that pulls from the xml strings packaged with the app
 * Packaged strings will be used by default unless overridden by a string in the dictionary
 */
@Singleton
class ResourceXmlDictionary @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : Dictionary {
    override fun getString(key: Int, args: Map<String,String>): String {
        return applicationContext.getString(key).applyArgs(args)
    }

    override fun getOptionalString(key: Int, args: Map<String,String>): String? {
        return Try { applicationContext.getString(key) }.getOrNull()?.applyArgs(args)
    }
}