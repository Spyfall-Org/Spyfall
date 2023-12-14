package com.dangerfield.libraries.ui.components.button

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.delay
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.color.ColorPrimitive
import spyfallx.ui.color.ColorToken
import spyfallx.ui.color.animateColorTokenAsState
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import kotlin.time.Duration.Companion.seconds

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
            animateColorTokenAsState(
                targetValue = targetColor,
                label = "BackgroundColorAnimation"
            )
        }.value
    }
    val contentColor by key(type, style) {
        animateColorTokenAsState(
            targetValue = type.contentColor(style, enabled),
            label = "ContentColorAnimation"
        )
    }

    val borderColor = when (style) {
        ButtonStyle.Filled -> null
        ButtonStyle.Outlined -> {
            val targetColor = when {
                !enabled -> OddOneOutTheme.colorScheme.surfaceDisabled
                else -> when (type) {
                    ButtonType.Accent -> OddOneOutTheme.colorScheme.accent
                    ButtonType.Regular -> OddOneOutTheme.colorScheme.onBackground
                }
            }
            key(type) {
                animateColorTokenAsState(targetColor, label = "BorderColorAnimation")
            }.value
        }

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
        contentPadding = when (style) {
            ButtonStyle.Filled,
            ButtonStyle.Outlined,
            -> size.padding(hasIcon = icon != null)

            ButtonStyle.NoBackground -> PaddingValues(Spacing.S200)
        },
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

enum class ButtonType {
    Accent,
    Regular,
}

enum class ButtonSize {
    Large,
    Small
}

enum class ButtonStyle {
    Filled,
    Outlined,
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

private val LocalButtonType = compositionLocalOf { ButtonType.Accent }
internal val LocalButtonSize = compositionLocalOf { ButtonSize.Large }
private val LocalButtonStyle = compositionLocalOf { ButtonStyle.Filled }

@Composable
@ReadOnlyComposable
private fun ButtonType.backgroundColor(
    style: ButtonStyle,
    enabled: Boolean,
): ColorToken.Color? = when (style) {
    ButtonStyle.Filled -> filledBackgroundColorToken(enabled)
    ButtonStyle.Outlined -> null
    ButtonStyle.NoBackground -> null
}

@Composable
@ReadOnlyComposable
private fun ButtonType.filledBackgroundColorToken(enabled: Boolean) = when {
    !enabled -> OddOneOutTheme.colorScheme.surfaceDisabled
    else -> when (this) {
        ButtonType.Accent -> OddOneOutTheme.colorScheme.accent
        ButtonType.Regular -> OddOneOutTheme.colorScheme.surfaceSecondary
    }
}

@Composable
@ReadOnlyComposable
private fun ButtonType.contentColor(style: ButtonStyle, enabled: Boolean): ColorToken.Color =
    when (style) {
        ButtonStyle.Filled -> when {
            !enabled -> OddOneOutTheme.colorScheme.onSurfaceDisabled
            else -> when (this) {
                ButtonType.Accent -> OddOneOutTheme.colorScheme.onAccent
                ButtonType.Regular -> OddOneOutTheme.colorScheme.text
            }
        }

        ButtonStyle.Outlined -> when {
            !enabled -> OddOneOutTheme.colorScheme.textDisabled
            else -> when (this) {
                ButtonType.Accent -> OddOneOutTheme.colorScheme.accent
                ButtonType.Regular -> OddOneOutTheme.colorScheme.text
            }
        }

        ButtonStyle.NoBackground -> when {
            !enabled -> OddOneOutTheme.colorScheme.textDisabled
            else -> when (this) {
                ButtonType.Accent -> OddOneOutTheme.colorScheme.accent
                ButtonType.Regular -> OddOneOutTheme.colorScheme.text
            }
        }
    }

@Preview(device = "spec:id=reference_phone,shape=Normal,width=1200,height=2000,unit=dp,dpi=200")
@Composable
private fun ButtonPreview(
    @PreviewParameter(ButtonPreviewParameterProvider::class) state: ButtonPreviewParameter,
) {
    PreviewContent(
        contentPadding = PaddingValues(Spacing.S200),
        isDarkMode = state.inverse,
        accentColor = ColorPrimitive.CherryPop700,
        showBackground = true,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S900)) {
            @Composable
            fun ButtonRows(
                style: ButtonStyle,
                fill: Boolean,
            ) {
                ButtonRow { type, icon, enabled ->
                    Row {
                        var e by remember(enabled) {
                            mutableStateOf(enabled)
                        }
                        if (enabled && !e) {
                            LaunchedEffect(Unit) {
                                delay(1.seconds)
                                e = true
                            }
                        }
                        Button(
                            onClick = { e = false },
                            modifier = if (fill) Modifier.fillMaxWidth() else Modifier,
                            icon = if (icon) SpyfallIcon.Check("") else null,
                            enabled = e,
                            type = type,
                            size = state.size,
                            style = style
                        ) {
                            Text("${state.size} ${style.name}")
                        }
                    }
                }
            }

            Text(text = "Button Size: ${state.size}", typographyToken = OddOneOutTheme.typography.Display.D1000)
            Text(text = "Mode:" + if (state.inverse) "Dark" else "Light", typographyToken = OddOneOutTheme.typography.Display.D1000)


            Row {
                ProvideTextConfig(OddOneOutTheme.typography.Heading.H700) {
                    Text("Accent", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Regular", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Disabled", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
            for (style in ButtonStyle.values()) {
                ButtonRows(style = style, fill = false)
                ButtonRows(style = style, fill = true)
            }
        }
    }
}

@Composable
private fun ButtonRow(
    modifier: Modifier = Modifier,
    button: @Composable (type: ButtonType, icon: Boolean, enabled: Boolean) -> Unit,
) {
    @Composable
    fun Row(icon: Boolean) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.S500),
            horizontalArrangement = Arrangement.spacedBy(Spacing.S900)
        ) {
            for (type in ButtonType.values()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    button(type, icon, true)
                }
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                button(ButtonType.Accent, icon, false)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.S500)) {
        Row(icon = false)
        Row(icon = true)
    }
}

private data class ButtonPreviewParameter(
    val inverse: Boolean,
    val size: ButtonSize,
)

private class ButtonPreviewParameterProvider : PreviewParameterProvider<ButtonPreviewParameter> {
    override val values: Sequence<ButtonPreviewParameter>
        get() = sequenceOf(false, true)
            .flatMap { inverse ->
                ButtonSize.values()
                    .asSequence()
                    .map { size ->
                        ButtonPreviewParameter(
                            inverse = inverse,
                            size = size
                        )
                    }
            }
}
