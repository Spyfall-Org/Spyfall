package com.dangerfield.libraries.ui.components.button

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.Border
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.Point
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Radius
import com.dangerfield.libraries.ui.StandardBorderWidth
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.icon.SmallIcon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.text.TextConfig
import com.dangerfield.libraries.ui.innerShadow
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.then
import com.dangerfield.libraries.ui.thenIf
import kotlin.math.sqrt

@Composable
internal fun BasicButton(
    backgroundColor: ColorResource?,
    borderColor: ColorResource?,
    contentColor: ColorResource,
    size: ButtonSize,
    style: ButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: SpyfallIcon? = null,
    contentPadding: PaddingValues = size.padding(hasIcon = icon != null),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.thenIf(enabled) {
                bounceClick(
                    mutableInteractionSource = interactionSource,
                    onClick = onClick
                )
            }
        ) {
            if (backgroundColor != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 2.dp)
                        .clip(Radii.Button.shape)
                        .background(Color.Black.copy(alpha = 0.3f)) // Darker shade for the elevation effect
                )
            }


            Surface(
                modifier = modifier
                    .then {
                        if (backgroundColor != null) {
                            innerShadow(
                                cornersRadius = 50.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                spread = 0.dp,
                                offsetY = BackgroundOffsetY,
                                offsetX = BackgroundOffsetX
                            )
                        } else {
                            this
                        }
                    }
                    .semantics { role = Role.Button },
                radius = Radii.Button,
                elevation = if (backgroundColor != null) Elevation.Button else Elevation.None,
                color = backgroundColor,
                contentColor = contentColor,
                border = borderColor?.let { Border(it, OutlinedButtonBorderWidth) },
                contentPadding = contentPadding
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        ButtonIconSpacing,
                        Alignment.CenterHorizontally
                    )
                ) {
                    if (icon != null) {
                        SmallIcon(
                            spyfallIcon = icon
                        )
                    }

                    ProvideTextConfig(size.textConfig(style), content = content)
                }
            }

            if (enabled && style == ButtonStyle.Background) {
                drawShineSquiggles()
            }
        }
    }
}

private val BackgroundOffsetX = -3.dp
private val BackgroundOffsetY = 3.dp

@Composable
private fun BoxScope.drawShineSquiggles(radius: Radius = Radii.Button) {
    val density = LocalDensity.current

    Canvas(modifier = Modifier.Companion.matchParentSize()) {
        val width = this.size.width
        val height = this.size.height

        val cornerRadiusOffset = calculateCornerOffset(
            radius.cornerSize,
            density,
            width,
            height
        )

        val topRightSquiggle =
            Path().apply {
                val topOfSquiggle = Point(
                    width - cornerRadiusOffset.x - 14.dp.toPx(),
                    cornerRadiusOffset.y
                )

                val bottomOfSquiggle = topOfSquiggle.offset(x = 10.dp.toPx(), y = 8.dp.toPx())

                val arcControl =
                    topOfSquiggle.midpoint(bottomOfSquiggle).offset(x = 2.dp.toPx(), y = -1.dp.toPx())

                moveTo(topOfSquiggle.x, topOfSquiggle.y)

                quadraticBezierTo(
                    arcControl.x, arcControl.y,
                    bottomOfSquiggle.x, bottomOfSquiggle.y
                )

                val control2 = arcControl.offset(2.dp.toPx(), -2.dp.toPx())

                quadraticBezierTo(
                    control2.x, control2.y,
                    topOfSquiggle.x, topOfSquiggle.y
                )
            }

        val bottomLeftSquiggle = Path().apply {
            val bottomLeftStart = Point(
                cornerRadiusOffset.x + 10.dp.toPx(),
                height - cornerRadiusOffset.y
            )

            moveTo(bottomLeftStart.x, bottomLeftStart.y)

            val topOfSquiggle = bottomLeftStart.offset(x = -10.dp.toPx(), y = -10.dp.toPx())

            val arcControl =
                bottomLeftStart.midpoint(topOfSquiggle).offset(x = -5.dp.toPx(), y = 5.dp.toPx())

            quadraticBezierTo(
                arcControl.x, arcControl.y,
                topOfSquiggle.x, topOfSquiggle.y
            )

            val control2 = arcControl.offset(2.dp.toPx(), -2.dp.toPx())

            quadraticBezierTo(
                control2.x, control2.y,
                bottomLeftStart.x, bottomLeftStart.y
            )
        }

        drawPath(path = topRightSquiggle, color = Color.White)
        drawPath(path = bottomLeftSquiggle, color = Color.White)
    }
}

fun calculateCornerOffset(
    cornerSize: CornerSize,
    density: Density,
    contentWidth: Float,
    contentHeight: Float
): Offset {
    val cornerSizepx = cornerSize.toPx(Size(contentWidth, contentHeight), density)

    val radius = cornerSizepx

    val offset = radius - (radius * sqrt(2.0f) / 2)

    return Offset(offset, offset)
}


private fun ButtonSize.textConfig(style: ButtonStyle): TextConfig = when (this) {
    ButtonSize.Small -> when (style) {
        ButtonStyle.Background -> SmallButtonTextConfig
        ButtonStyle.NoBackground -> SmallTextButtonTextConfig
    }

    ButtonSize.Large -> when (style) {
        ButtonStyle.Background -> LargeButtonTextConfig
        ButtonStyle.NoBackground -> LargeTextButtonTextConfig
    }

    ButtonSize.ExtraSmall -> when (style) {
        ButtonStyle.Background -> ExtraSmallButtonTextConfig
        ButtonStyle.NoBackground -> ExtraSmallButtonTextConfig
    }
}

internal fun ButtonSize.padding(hasIcon: Boolean): PaddingValues =
    when (this) {
        ButtonSize.Small -> if (hasIcon) SmallButtonWithIconPadding else SmallButtonPadding
        ButtonSize.Large -> if (hasIcon) LargeButtonWithIconPadding else LargeButtonPadding
        ButtonSize.ExtraSmall -> if (hasIcon) ExtraSmallButtonWithIconPadding else ExtraSmallButtonPadding
    }

private val SmallButtonTextConfig = TextConfig(
    typography = OddOneOutTheme.typography.Label.L600,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val ExtraSmallButtonTextConfig = TextConfig(
    typography = OddOneOutTheme.typography.Label.L400,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val SmallTextButtonTextConfig = TextConfig(
    typography = OddOneOutTheme.typography.Body.B600.SemiBold,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val LargeButtonTextConfig = TextConfig(
    typography = OddOneOutTheme.typography.Label.L800.SemiBold,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)


private val LargeButtonVerticalPadding = Dimension.D700
private val SmallButtonVerticalPadding = Dimension.D500
private val ExtraButtonVerticalPadding = Dimension.D500

private val LargeTextButtonTextConfig = TextConfig(
    typography = OddOneOutTheme.typography.Body.B700.SemiBold,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val LargeButtonPadding = PaddingValues(
    horizontal = Dimension.D900,
    vertical = LargeButtonVerticalPadding
)

private val LargeButtonWithIconPadding = PaddingValues(
    top = LargeButtonVerticalPadding,
    bottom = LargeButtonVerticalPadding,
    start = Dimension.D700,
    end = Dimension.D900,
)

private val SmallButtonPadding = PaddingValues(
    horizontal = Dimension.D800,
    vertical = SmallButtonVerticalPadding
)

private val SmallButtonWithIconPadding = PaddingValues(
    start = Dimension.D600,
    top = SmallButtonVerticalPadding,
    end = Dimension.D800,
    bottom = SmallButtonVerticalPadding
)

private val ExtraSmallButtonPadding = PaddingValues(
    horizontal = Dimension.D600,
    vertical = ExtraButtonVerticalPadding
)

private val ExtraSmallButtonWithIconPadding = PaddingValues(
    start = Dimension.D500,
    top = ExtraButtonVerticalPadding,
    end = Dimension.D500,
    bottom = ExtraButtonVerticalPadding
)

private val ButtonIconSpacing = Dimension.D200
private val OutlinedButtonBorderWidth = StandardBorderWidth

@Preview
@Composable
private fun LargeButton() {
    Preview {
        BasicButton(
            backgroundColor = OddOneOutTheme.colors.accent,
            borderColor = null,
            contentColor = OddOneOutTheme.colors.onAccent,
            size = ButtonSize.Large,
            style = ButtonStyle.Background,
            onClick = {},
            content = { Text(text = "Button") }
        )
    }
}

@Preview
@Composable
private fun SmallButton() {
    Preview {
        BasicButton(
            backgroundColor = OddOneOutTheme.colors.accent,
            borderColor = null,
            contentColor = OddOneOutTheme.colors.onAccent,
            size = ButtonSize.Small,
            style = ButtonStyle.Background,
            onClick = {},
            content = { Text(text = "Button") }
        )
    }
}

@Preview
@Composable
private fun ExtraSmallButton() {
    Preview {
        BasicButton(
            backgroundColor = OddOneOutTheme.colors.accent,
            borderColor = null,
            contentColor = OddOneOutTheme.colors.onAccent,
            size = ButtonSize.ExtraSmall,
            style = ButtonStyle.Background,
            onClick = {},
            content = { Text(text = "Button") }
        )
    }
}

@Preview
@Composable
private fun LargeButtonNOBackgroun() {
    Preview {
        BasicButton(
            backgroundColor = null,
            borderColor = null,
            contentColor = OddOneOutTheme.colors.onAccent,
            size = ButtonSize.Large,
            style = ButtonStyle.NoBackground,
            onClick = {},
            content = { Text(text = "Button") }
        )
    }
}