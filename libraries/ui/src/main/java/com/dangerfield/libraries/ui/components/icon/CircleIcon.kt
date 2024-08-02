package com.dangerfield.libraries.ui.components.icon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.thenIf

@Composable
fun CircleIcon(
    icon: SpyfallIcon,
    iconSize: IconSize,
    padding: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: ColorResource = OddOneOutTheme.colors.surfacePrimary,
    contentColor: ColorResource = OddOneOutTheme.colors.onSurfacePrimary,
    elevation: Elevation = Elevation.None,
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        contentPadding = PaddingValues(padding),
        elevation = elevation,
        radius = Radii.Round,
        modifier = modifier.thenIf(onClick != null) {
            Modifier.clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
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
    Preview(showBackground = false) {
        CircleIcon(
            icon = SpyfallIcon.Android("Test"),
            iconSize = IconSize.Large,
            padding = Dimension.D400,
            backgroundColor = OddOneOutTheme.colors.background,
            contentColor = OddOneOutTheme.colors.onBackground
        )
    }
}
