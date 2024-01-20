package com.dangerfield.libraries.dictionary

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// TODO add lint error for accessing string via context rather than dictionary
val LocalDictionary = staticCompositionLocalOf<Dictionary> {
    error("No LocalDictionary provided")
}

// TODO make a function that will trigger a recompose if the value changes.
@Composable
fun dictionaryString(@StringRes id: Int, args: Map<String,String> = emptyMap()): String {
    return LocalDictionary.current.getString(id, args)
}
