package spyfallx.coreui

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
import androidx.compose.ui.unit.dp
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.color.background
import spyfallx.coreui.theme.SpyfallTheme

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
    showBackground: Boolean = false,
    accentColor: ColorPrimitive = ColorPrimitive.CherryPop700,
    content: @Composable BoxScope.() -> Unit,
) {
    var context = LocalContext.current
    if (context !is ContextThemeWrapper && context !is android.view.ContextThemeWrapper) {
        context = ContextThemeWrapper(
            LocalContext.current,
            R.style.Theme_Spyfall
        )
    }
    CompositionLocalProvider(LocalContext provides context) {
        SpyfallTheme(isDarkMode = isDarkMode, accentColor = accentColor) {
            Box(
                modifier = modifier
                    .thenIf(showBackground) { background(SpyfallTheme.colorScheme.background) }
                    .padding(contentPadding)
            ) {
                content()
            }
        }
    }
}