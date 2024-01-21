package com.dangerfield.libraries.ui.preview

import android.content.res.Configuration
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.dictionary.applyArgs
import com.dangerfield.libraries.ui.color.ColorPrimitive
import spyfallx.ui.R
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.color.ThemeColor
import oddoneout.core.Try
import spyfallx.ui.thenIf

/**
 * A composable that is suitable as the root for any composable preview
 *
 * It will set up the theme and some suitable defaults like a background color.
 */
@Composable
fun PreviewContent(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showBackground: Boolean = true,
    themeColor: ColorPrimitive = ThemeColor.entries.random().colorPrimitive,
    content: @Composable BoxScope.() -> Unit,
) {
    var context = LocalContext.current
    if (context !is ContextThemeWrapper && context !is android.view.ContextThemeWrapper) {
        context = ContextThemeWrapper(
            LocalContext.current,
            R.style.Theme_Spyfall
        )
    }

    val previewDictionary = object: Dictionary {
        override fun getString(key: Int, args: Map<String,String>): String = Try {
            context.getText(key).toString().applyArgs(args)
        }.getOrNull() ?: "DNE"

        override fun getOptionalString(key: Int, args: Map<String,String>): String? = Try {
            context.getText(key).toString().applyArgs(args)
        }.getOrNull()
    }

    CompositionLocalProvider(
        LocalContext provides context,
        LocalDictionary provides previewDictionary,
    ) {
        OddOneOutTheme(isDarkMode = isDarkMode, themeColor = themeColor) {
            Box(
                modifier = modifier
                    .thenIf(showBackground) { background(OddOneOutTheme.colorScheme.background) }
                    .padding(contentPadding)
            ) {
                content()
            }
        }
    }
}

/**
 * Multipreview annotation that represents light and dark themes. Add this annotation to a
 * composable to render the both themes.
 */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class ThemePreviews
