package com.dangerfield.libraries.dictionary

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalDictionary = staticCompositionLocalOf<Dictionary> {
    error("No LocalDictionary provided")
}

/**
 * Retrieves a string from the dictionary, throws an error if it doesn't exist
 */
@Composable
fun dictionaryString(@StringRes id: Int, vararg args: Pair<String,String>): String {
    val dictionary = LocalDictionary.current
    return remember(id, args) { dictionary.getString(id, args.toMap()) }
}
