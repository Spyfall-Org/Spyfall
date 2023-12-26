package com.dangerfield.libraries.ui.components.button

import com.dangerfield.libraries.ui.Border
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.StandardBorderWidth
import spyfallx.ui.color.ColorToken
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.text.TextConfig
import com.dangerfield.libraries.ui.components.icon.SmallIcon

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
        Surface(
            onClick = onClick,
            modifier = modifier.semantics { role = Role.Button },
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
                horizontalArrangement = Arrangement.spacedBy(ButtonIconSpacing, Alignment.CenterHorizontally)
            ) {
                if (icon != null) {
                    SmallIcon(
                        spyfallIcon = icon
                    )
                }
                ProvideTextConfig(size.textConfig(style), content = content)
            }
        }
    }
}

private fun ButtonSize.textConfig(style: ButtonStyle): TextConfig = when (this) {
    ButtonSize.Small -> when (style) {
        ButtonStyle.Filled,
        ButtonStyle.Outlined,
        -> SmallButtonTextConfig

        ButtonStyle.NoBackground -> SmallTextButtonTextConfig
    }

    ButtonSize.Large -> when (style) {
        ButtonStyle.Filled,
        ButtonStyle.Outlined,
        -> LargeButtonTextConfig

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
                backgroundColor = OddOneOutTheme.colorScheme.surfacePrimary,
                contentColor = OddOneOutTheme.colorScheme.onSurfacePrimary
            )
            ButtonRow(
                size = ButtonSize.Small,
                style = ButtonStyle.Filled,
                backgroundColor = OddOneOutTheme.colorScheme.accent,
                contentColor = OddOneOutTheme.colorScheme.onAccent
            )
            ButtonRow(
                size = ButtonSize.Large,
                backgroundColor = null,
                style = ButtonStyle.Outlined,
                contentColor = OddOneOutTheme.colorScheme.text,
                borderColor = OddOneOutTheme.colorScheme.surfacePrimary
            )
            ButtonRow(
                size = ButtonSize.Small,
                style = ButtonStyle.Outlined,
                backgroundColor = null,
                contentColor = OddOneOutTheme.colorScheme.text,
                borderColor = OddOneOutTheme.colorScheme.surfacePrimary
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