package com.dangerfield.libraries.ui.components.header

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    modifier: Modifier = Modifier,
    title: String? = null,
    titleAlignment: Alignment.Horizontal = Alignment.Start,
    onNavigateBack: (() -> Unit)? = null,
    typographyToken: TypographyResource = OddOneOutTheme.typography.Heading.H1000,
    backgroundColor: Color = OddOneOutTheme.colors.background.color,
    actions: @Composable RowScope.() -> Unit = {},
    scrollState: ScrollState? = null,
    liftOnScroll: Boolean = scrollState != null,
    navigationIcon: SpyfallIcon = SpyfallIcon.ChevronLeft("Navigate back"),
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {

    when(titleAlignment) {
        Alignment.Start -> {
            MaterialTopAppBar(
                title = {
                    title?.let { Text(text = it, typography = typographyToken) }
                },
                modifier = modifier
                    .thenIf(liftOnScroll) { elevateOnScroll(scrollState)},
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(
                            size = IconButton.Size.Large,
                            icon = navigationIcon,
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
        Alignment.CenterHorizontally -> {
            CenterAlignedTopAppBar(
                title = {
                    title?.let { Text(text = it, typography = typographyToken) }
                },
                modifier = modifier
                    .thenIf(liftOnScroll) { elevateOnScroll(scrollState)},
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(
                            size = IconButton.Size.Large,
                            icon = navigationIcon,
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
        Alignment.End -> {
            throw IllegalArgumentException("Alignment.End is not supported for titleAlignment")
        }
    }
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

@Preview
@Composable
private fun PreviewHeaderCenterAligned() {
    Preview {
        Header(
            title = "Heading Title",
            titleAlignment = Alignment.CenterHorizontally
        )
    }
}

