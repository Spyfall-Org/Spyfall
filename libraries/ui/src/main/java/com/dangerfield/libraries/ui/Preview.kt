package com.dangerfield.libraries.ui

import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import oddoneout.core.Catching
import oddoneout.core.applyArgs
import spyfallx.ui.R

/**
 * A composable that is suitable as the root for any composable preview
 *
 * It will set up the theme and some suitable defaults like a background color.
 */
@Composable
fun Preview(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showBackground: Boolean = true,
    themeColor: ColorResource = ThemeColor.entries.random().colorResource,
    content: @Composable BoxScope.() -> Unit,
) {
    var context = LocalContext.current
    if (context !is ContextThemeWrapper && context !is android.view.ContextThemeWrapper) {
        context = ContextThemeWrapper(
            LocalContext.current,
            R.style.Theme_Spyfall
        )
    }

    val previewDictionary = object : Dictionary {
        override fun getString(key: Int, args: Map<String, String>): String = Catching {
            context.getText(key).toString().applyArgs(args)
        }.getOrNull() ?: "DNE"

        override fun getOptionalString(key: Int, args: Map<String, String>): String? = Catching {
            context.getText(key).toString().applyArgs(args)
        }.getOrNull()
    }

    CompositionLocalProvider(
        LocalContext provides context,
        LocalDictionary provides previewDictionary,
    ) {
        OddOneOutTheme(themeColor = themeColor) {
            Box(
                modifier = modifier
                    .thenIf(showBackground) {
                        background(OddOneOutTheme.colors.background.color)
                    }
                    .padding(contentPadding)
            ) {
                content()
            }
        }
    }
}