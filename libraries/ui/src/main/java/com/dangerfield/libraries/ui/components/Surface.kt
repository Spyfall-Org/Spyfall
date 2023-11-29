package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spyfallx.ui.Border
import spyfallx.ui.Elevation
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Radii
import spyfallx.ui.Radius
import spyfallx.ui.Spacing
import spyfallx.ui.ThrottledCallback
import spyfallx.ui.color.ColorToken
import spyfallx.ui.color.ProvideContentColor
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
@NonRestartableComposable
fun Surface(
    color: ColorToken.Color?,
    contentColor: ColorToken.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    radius: Radius = Radii.Default,
    elevation: Elevation = Elevation.None,
    border: Border? = null,
    alpha: Float = 1f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = rememberRipple(color = contentColor.color),
    role: Role? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    val throttledCallback = remember { ThrottledCallback(onClick) }

    Box(
        modifier = modifier
            .surface(
                color = color,
                radius = radius,
                elevation = if (color != null) elevation else Elevation.None,
                border = border,
                alpha = alpha
            )
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                enabled = enabled,
                role = role,
                onClick = throttledCallback::invoke
            )
            .padding(contentPadding),
        propagateMinConstraints = true
    ) {
        ProvideContentColor(contentColor, content)
    }
}

@Composable
@NonRestartableComposable
fun Surface(
    color: ColorToken?,
    contentColor: ColorToken.Color,
    modifier: Modifier = Modifier,
    radius: Radius = Radii.Default,
    elevation: Elevation = Elevation.None,
    border: Border? = null,
    alpha: Float = 1f,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .surface(
                color = color,
                radius = radius,
                elevation = elevation,
                border = border,
                alpha = alpha
            )
            .semantics(mergeDescendants = false) {
                isTraversalGroup = true
            }
            // This prevents siblings that are underneath this surface from being receiving pointer events
            .pointerInput(Unit) {}
            .padding(contentPadding),
        propagateMinConstraints = true
    ) {
        ProvideContentColor(contentColor, content)
    }
}

private fun Modifier.surface(
    color: ColorToken?,
    radius: Radius,
    elevation: Elevation,
    border: Border?,
    alpha: Float = 1f,
) = this
    .background(
        color = color ?: ColorToken.Color.Unspecified,
        radius = radius,
        elevation = elevation,
        clip = true,
        border = border,
        alpha = alpha
    )

@Preview
@Composable
private fun SurfacePreview() {
    PreviewContent {
        Surface(
            color = SpyfallTheme.colorScheme.background,
            contentColor = SpyfallTheme.colorScheme.text,
            contentPadding = PaddingValues(Spacing.S900)
        ) {
            Text("Hello")
        }
    }
}

@Preview
@Composable
private fun ClickableSurfacePreview() {
    PreviewContent {
        Surface(
            color = SpyfallTheme.colorScheme.background,
            contentColor = SpyfallTheme.colorScheme.text,
            onClick = {},
            radius = Radii.Banner,
            contentPadding = PaddingValues(Spacing.S900)
        ) {
            Text("Hello")
        }
    }
}
