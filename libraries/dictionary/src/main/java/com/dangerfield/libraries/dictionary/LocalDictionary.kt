package com.dangerfield.libraries.dictionary

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalDictionary = staticCompositionLocalOf<Dictionary> {
    error("No LocalDictionary provided")
}

@Composable
fun dictionaryString(@StringRes id: Int, vararg args: Pair<String,String>): String {
    val dictionary = LocalDictionary.current
    return remember(args) { dictionary.getString(id, args.toMap()) }
}
