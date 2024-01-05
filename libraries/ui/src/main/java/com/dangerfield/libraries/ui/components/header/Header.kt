package com.dangerfield.libraries.ui.components.header

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.typography.TypographyToken
import com.dangerfield.libraries.ui.Elevation
import spyfallx.ui.thenIf
import androidx.compose.material3.TopAppBar as MaterialTopAppBar

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    typographyToken: TypographyToken = OddOneOutTheme.typography.Heading.H1000,
    backgroundColor: Color = OddOneOutTheme.colorScheme.background.color,
    actions: @Composable RowScope.() -> Unit = {},
    scrollState: ScrollState? = null,
    liftOnScroll: Boolean = scrollState != null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    MaterialTopAppBar(
        title = {
            Text(text = title, typographyToken = typographyToken)
        },
        modifier = modifier
            .thenIf(liftOnScroll) { liftOnScroll(scrollState)},
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(
                    size = IconButton.Size.Large,
                    icon = SpyfallIcon.ChevronLeft("Navigate back"),
                    onClick = onNavigateBack
                )
            }
        },
        actions = actions,
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            scrolledContainerColor = backgroundColor,
            navigationIconContentColor = OddOneOutTheme.colorScheme.onBackground.color,
            titleContentColor = OddOneOutTheme.colorScheme.onBackground.color,
            actionIconContentColor = OddOneOutTheme.colorScheme.onBackground.color
        )
    )
}

private fun Modifier.liftOnScroll(
    scrollState: ScrollState?,
): Modifier {

    checkNotNull(scrollState) {
        "ScrollState should not be null when liftOnScroll is true"
    }

    return composed {
        val elevation by animateDpAsState(
            if (scrollState.canScrollBackward) {
                Elevation.AppBar.dp
            } else {
                0.dp
            }, label = ""
        )
        Modifier.shadow(elevation)
    }
}

@Preview
@Composable
private fun PreviewHeader() {
    PreviewContent {
        Header(
            title = "Heading Title",
        )
    }
}

