package com.dangerfield.libraries.ui.components.icon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import spyfallx.ui.color.ColorToken

@Composable
fun CircularIcon(
    icon: SpyfallIcon,
    iconSize: IconSize,
    padding: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: ColorToken.Color = OddOneOutTheme.colorScheme.surfacePrimary,
    contentColor: ColorToken.Color  = OddOneOutTheme.colorScheme.onSurfacePrimary,
    elevation: Elevation = Elevation.None,
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        contentPadding = PaddingValues(padding),
        elevation = elevation,
        radius = Radii.Round,
        modifier = modifier.clickable(enabled = onClick != null) {
            onClick?.invoke()
        }
    ) {
        Icon(
            spyfallIcon = icon,
            iconSize = iconSize
        )
    }
}

@Preview
@Composable
private fun CircularIconPreview() {
    PreviewContent(showBackground = false) {
        CircularIcon(
            icon = SpyfallIcon.Android("Test"),
            iconSize = IconSize.Large,
            padding = Spacing.S400,
            backgroundColor = OddOneOutTheme.colorScheme.background,
            contentColor = OddOneOutTheme.colorScheme.onBackground
        )
    }
}
