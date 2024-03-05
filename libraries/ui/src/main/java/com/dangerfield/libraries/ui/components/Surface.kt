package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.Border
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Radius
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.ProvideContentColor
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.inset
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.thenIf
import com.dangerfield.libraries.ui.thenIfNotNull

@Composable
@NonRestartableComposable
fun Surface(
    color: ColorResource?,
    contentColor: ColorResource,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    radius: Radius = Radii.Default,
    elevation: Elevation = Elevation.None,
    border: Border? = null,
    alpha: Float = 1f,
    onClick: () -> Unit,
    bounceScale: Float = 0.95f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = rememberRipple(color = contentColor.color),
    role: Role? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .bounceClick(
                enabled = enabled,
                scaleDown = bounceScale,
                indication = indication,
                mutableInteractionSource = interactionSource,
                onClick = onClick,
            )
            .thenIfNotNull(role) {
                semantics {
                    this.role = it
                }
            }
            .background(
                color = color,
                shape = radius.shape,
                elevation = elevation,
                clip = true,
                alpha = alpha,
                border = border
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
    color: ColorResource?,
    contentColor: ColorResource,
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
            .background(
                color = color,
                shape = radius.shape,
                elevation = elevation,
                clip = true,
                alpha = alpha,
                border = border
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

private fun Modifier.background(
    color: ColorResource?,
    shape: Shape,
    elevation: Elevation,
    clip: Boolean,
    alpha: Float,
    border: Border?,
): Modifier = inspectable(
    androidx.compose.ui.platform.debugInspectorInfo {
        name = "background"
        properties["color"] = color
        properties["shape"] = shape
        properties["elevation"] = elevation.dp
        properties["clip"] = clip
        properties["alpha"] = alpha
        properties["border"] = border
    }
) {
    val backgroundShape = if (border == null || border.color.color.alpha < 0.99f) shape else shape.inset(border.width / 2f)
    this
        .thenIf(elevation > Elevation.None || alpha < 1f) {
            graphicsLayer {
                if (elevation > Elevation.None) {
                    shadowElevation = elevation.dp.toPx()
                    spotShadowColor = ColorResource.Black900.color
                    ambientShadowColor = ColorResource.Black900.color
                }
                this.alpha = alpha
                this.shape = shape
            }
        }
        .thenIfNotNull(border) {
            this.border(width = it.width, color = it.color.color, shape = shape)
        }
        .thenIfNotNull(color) {
            this.background(color = it.color, shape = shape)
        }
        .thenIf(clip) { clip(backgroundShape) }
}

@Preview
@Composable
private fun SurfacePreview() {
    Preview {
        Surface(
            color = OddOneOutTheme.colors.background,
            contentColor = OddOneOutTheme.colors.text,
            contentPadding = PaddingValues(Dimension.D900)
        ) {
            Text("Hello")
        }
    }
}

@Preview
@Composable
private fun ClickableSurfacePreview() {
    Preview {
        Surface(
            color = OddOneOutTheme.colors.background,
            contentColor = OddOneOutTheme.colors.text,
            radius = Radii.Banner,
            contentPadding = PaddingValues(Dimension.D900)
        ) {
            Text("Hello")
        }
    }
}

@Preview
@Composable
private fun ClickableSurfacePreviewNoColor() {
    Preview(showBackground = false) {
        Surface(
            color = null,
            contentColor = OddOneOutTheme.colors.text,
            radius = Radii.Banner,
            contentPadding = PaddingValues(Dimension.D900)
        ) {
            Text("Hello")
        }
    }
}
