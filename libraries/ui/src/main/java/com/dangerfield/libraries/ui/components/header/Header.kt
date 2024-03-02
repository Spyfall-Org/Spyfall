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
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.typography.TypographyResource
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.thenIf
import androidx.compose.material3.TopAppBar as MaterialTopAppBar

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    typographyToken: TypographyResource = OddOneOutTheme.typography.Heading.H1000,
    backgroundColor: Color = OddOneOutTheme.colors.background.color,
    actions: @Composable RowScope.() -> Unit = {},
    scrollState: ScrollState? = null,
    liftOnScroll: Boolean = scrollState != null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    MaterialTopAppBar(
        title = {
            Text(text = title, typography = typographyToken)
        },
        modifier = modifier
            .thenIf(liftOnScroll) { elevateOnScroll(scrollState)},
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
            navigationIconContentColor = OddOneOutTheme.colors.onBackground.color,
            titleContentColor = OddOneOutTheme.colors.onBackground.color,
            actionIconContentColor = OddOneOutTheme.colors.onBackground.color
        )
    )
}

private fun Modifier.elevateOnScroll(
    scrollState: ScrollState?,
): Modifier {

    checkNotNull(scrollState) {
        "ScrollState should not be null when liftOnScroll is true"
    }

    return this.composed {
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
    Preview {
        Header(
            title = "Heading Title",
        )
    }
}

