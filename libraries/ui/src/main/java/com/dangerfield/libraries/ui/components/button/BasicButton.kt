package com.dangerfield.libraries.ui.components.button

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.Point
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.StandardBorderWidth
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.icon.SmallIcon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.text.TextConfig
import com.dangerfield.libraries.ui.innerShadow
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.color.ColorToken
import spyfallx.ui.then
import spyfallx.ui.thenIf
import kotlin.math.sqrt

@Composable
internal fun BasicButton(
    backgroundColor: ColorToken.Color?,
    borderColor: ColorToken.Color?,
    contentColor: ColorToken.Color,
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
                bounceClick(mutableInteractionSource = interactionSource,)
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
                onClick = onClick,
                modifier = modifier
                    .then {
                        if (backgroundColor != null) {
                            innerShadow(
                                cornersRadius = 50.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                spread = 0.dp,
                                offsetY = 3.dp,
                                offsetX = -3.dp
                            )
                        } else {
                            this
                        }
                    }
                    .semantics { role = Role.Button },
                enabled = enabled,
                radius = Radii.Button,
                elevation = Elevation.Fixed,
                color = backgroundColor,
                contentColor = contentColor,
                border = borderColor?.let { Border(it, OutlinedButtonBorderWidth) },
                interactionSource = interactionSource,
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

            if (enabled && style == ButtonStyle.Filled) {
                drawShineSquiggles()
            }
        }
    }
}


@Composable
private fun BoxScope.drawShineSquiggles() {
    val density = LocalDensity.current

    Canvas(modifier = Modifier.Companion.matchParentSize()) {
        val width = this.size.width
        val height = this.size.height

        val cornerRadiusOffset = calculateCornerOffset(
            Radii.Button.cornerSize,
            density,
            width,
            height
        )

        val topRightSquiggle =
            Path().apply {
                val topRightStart = Point(
                    width - cornerRadiusOffset.x - 13.dp.toPx(),
                    cornerRadiusOffset.y - 3.dp.toPx()
                )

                val topRightCorner = Point(
                    width - cornerRadiusOffset.x,
                    cornerRadiusOffset.y
                )

                moveTo(topRightStart.x, topRightStart.y)

                val bottomOfSquiggle = topRightStart.offset(x = 15.dp.toPx(), y = 20.dp.toPx())

                val arcControl = topRightCorner

                quadraticBezierTo(
                    arcControl.x, arcControl.y,
                    bottomOfSquiggle.x, bottomOfSquiggle.y
                )

                val control2 = topRightCorner.offset(-2.dp.toPx(), 4.dp.toPx())

                quadraticBezierTo(
                    control2.x, control2.y,
                    topRightStart.x, topRightStart.y
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

private fun calculateCornerOffset(
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
        ButtonStyle.Filled -> SmallButtonTextConfig
        ButtonStyle.NoBackground -> SmallTextButtonTextConfig
    }

    ButtonSize.Large -> when (style) {
        ButtonStyle.Filled -> LargeButtonTextConfig
        ButtonStyle.NoBackground -> LargeTextButtonTextConfig
    }
}

internal fun ButtonSize.padding(hasIcon: Boolean): PaddingValues =
    when (this) {
        ButtonSize.Small -> if (hasIcon) SmallButtonWithIconPadding else SmallButtonPadding
        ButtonSize.Large -> if (hasIcon) LargeButtonWithIconPadding else LargeButtonPadding
    }

private val SmallButtonTextConfig = TextConfig(
    typographyToken = OddOneOutTheme.typography.Label.L600,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val SmallTextButtonTextConfig = TextConfig(
    typographyToken = OddOneOutTheme.typography.Body.B600.SemiBold,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val LargeButtonTextConfig = TextConfig(
    typographyToken = OddOneOutTheme.typography.Label.L800.SemiBold,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val LargeTextButtonTextConfig = TextConfig(
    typographyToken = OddOneOutTheme.typography.Body.B700.SemiBold,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
)

private val LargeButtonPadding = PaddingValues(
    horizontal = Spacing.S900,
    vertical = Spacing.S600
)

private val LargeButtonWithIconPadding = PaddingValues(
    start = Spacing.S700,
    top = Spacing.S500,
    end = Spacing.S900,
    bottom = Spacing.S500
)

private val SmallButtonPadding = PaddingValues(
    horizontal = Spacing.S800,
    vertical = Spacing.S500
)

private val SmallButtonWithIconPadding = PaddingValues(
    start = Spacing.S600,
    top = Spacing.S300,
    end = Spacing.S800,
    bottom = Spacing.S300
)

private val ButtonIconSpacing = Spacing.S200
private val OutlinedButtonBorderWidth = StandardBorderWidth

@Preview
@Composable
private fun BasicButtonPreview() {
    PreviewContent(
        contentPadding = PaddingValues(Spacing.S200),
        showBackground = true
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.S500)
        ) {
            ButtonRow(
                size = ButtonSize.Large,
                style = ButtonStyle.Filled,
                backgroundColor = OddOneOutTheme.colorScheme.accent,
                contentColor = OddOneOutTheme.colorScheme.onAccent
            )
            ButtonRow(
                size = ButtonSize.Small,
                style = ButtonStyle.Filled,
                backgroundColor = OddOneOutTheme.colorScheme.accent,
                contentColor = OddOneOutTheme.colorScheme.onAccent
            )
        }
    }
}

@Composable
private fun ButtonRow(
    size: ButtonSize,
    style: ButtonStyle,
    backgroundColor: ColorToken.Color?,
    contentColor: ColorToken.Color,
    borderColor: ColorToken.Color? = null,
    content: @Composable () -> Unit = { Text("Text") },
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.S100)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            BasicButton(
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                contentColor = contentColor,
                size = size,
                style = style,
                onClick = {},
                content = content
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            BasicButton(
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                contentColor = contentColor,
                size = size,
                style = style,
                icon = SpyfallIcon.Info(""),
                onClick = {},
                content = content
            )
        }
    }
}
