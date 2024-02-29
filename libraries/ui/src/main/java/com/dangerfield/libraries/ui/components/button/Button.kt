package com.dangerfield.libraries.ui.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.animateColorResourceAsState
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: SpyfallIcon? = null,
    type: ButtonType = LocalButtonType.current,
    size: ButtonSize = LocalButtonSize.current,
    style: ButtonStyle = LocalButtonStyle.current,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val backgroundColor = type.backgroundColor(style, enabled)?.let { targetColor ->
        key(type, style) {
            animateColorResourceAsState(
                targetValue = targetColor,
                label = "BackgroundColorAnimation"
            )
        }.value
    }
    val contentColor by key(type, style) {
        animateColorResourceAsState(
            targetValue = type.contentColor(style, enabled),
            label = "ContentColorAnimation"
        )
    }

    val borderColor = when (style) {
        ButtonStyle.Background -> null
        ButtonStyle.NoBackground -> null
    }

    BasicButton(
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        contentColor = contentColor,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        size = size,
        style = style,
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

enum class ButtonType {
    Primary,
    Secondary,
}

enum class ButtonSize {
    Large,
    Small,
    ExtraSmall
}

enum class ButtonStyle {
    Background,
    NoBackground,
}

@Composable
fun ProvideButtonConfig(
    type: ButtonType = LocalButtonType.current,
    size: ButtonSize = LocalButtonSize.current,
    style: ButtonStyle = LocalButtonStyle.current,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalButtonType provides type,
        LocalButtonSize provides size,
        LocalButtonStyle provides style,
        content = content
    )
}

private val LocalButtonType = compositionLocalOf { ButtonType.Primary }
internal val LocalButtonSize = compositionLocalOf { ButtonSize.Large }
private val LocalButtonStyle = compositionLocalOf { ButtonStyle.Background }

@Composable
@ReadOnlyComposable
private fun ButtonType.backgroundColor(
    style: ButtonStyle,
    enabled: Boolean,
): ColorResource? = when (style) {
    ButtonStyle.Background -> filledBackgroundColorToken(enabled)
    ButtonStyle.NoBackground -> null
}

@Composable
@ReadOnlyComposable
private fun ButtonType.filledBackgroundColorToken(enabled: Boolean) = when {
    !enabled -> OddOneOutTheme.colors.surfaceDisabled
    else -> when (this) {
        ButtonType.Primary -> OddOneOutTheme.colors.accent
        ButtonType.Secondary -> OddOneOutTheme.colors.surfaceSecondary
    }
}

@Composable
@ReadOnlyComposable
private fun ButtonType.contentColor(style: ButtonStyle, enabled: Boolean): ColorResource =
    when (style) {
        ButtonStyle.Background -> when {
            !enabled -> OddOneOutTheme.colors.onSurfaceDisabled
            else -> when (this) {
                ButtonType.Primary -> OddOneOutTheme.colors.onAccent
                ButtonType.Secondary -> OddOneOutTheme.colors.text
            }
        }

        ButtonStyle.NoBackground -> when {
            !enabled -> OddOneOutTheme.colors.textDisabled
            else -> when (this) {
                ButtonType.Primary -> OddOneOutTheme.colors.accent
                ButtonType.Secondary -> OddOneOutTheme.colors.text
            }
        }
    }
