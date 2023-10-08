package spyfallx.coreui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    content: @Composable BoxScope.() -> Unit,
) {
    SpyfallTheme(isDarkMode = isDarkMode) {
        Box(
            modifier = modifier
                .thenIf(showBackground) { background(SpyfallTheme.colorScheme.backgroundPrimary) }
                .padding(contentPadding)
        ) {
            content()
        }

    }
}