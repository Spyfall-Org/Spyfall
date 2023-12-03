package com.dangerfield.libraries.ui.components.icon

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import com.dangerfield.libraries.ui.Radii
import spyfallx.ui.Spacing
import spyfallx.ui.color.ColorToken

@Composable
fun CircularIcon(
    icon: SpyfallIcon,
    iconSize: IconSize,
    padding: Dp,
    backgroundColor: ColorToken.Color,
    contentColor: ColorToken.Color,
    modifier: Modifier = Modifier,
    elevation: Elevation = Elevation.None,
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        contentPadding = PaddingValues(padding),
        elevation = elevation,
        radius = Radii.Round,
        modifier = modifier
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
            backgroundColor = SpyfallTheme.colorScheme.background,
            contentColor = SpyfallTheme.colorScheme.onBackground
        )
    }
}
