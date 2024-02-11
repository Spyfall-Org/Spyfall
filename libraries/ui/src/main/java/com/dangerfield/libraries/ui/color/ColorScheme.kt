@file:Suppress("MagicNumber")

package com.dangerfield.libraries.ui.color

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.Divider
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.thenIf
import com.dangerfield.libraries.ui.typography.Typography
import spyfallx.ui.color.ColorToken
import kotlin.reflect.KProperty1

@Suppress("LongParameterList")
@Immutable
class ColorScheme internal constructor(

    val accent: ColorToken.Color,
    val onAccent: ColorToken.Color,

    /* Backgrounds */
    val shadow: ColorToken.Color,
    val background: ColorToken.Color,
    val backgroundOverlay: ColorToken.Color,
    val onBackground: ColorToken.Color,
    val border: ColorToken.Color,
    val borderDisabled: ColorToken.Color,

    /* Texts */
    val text: ColorToken.Color,
    val textDisabled: ColorToken.Color,
    val textWarning: ColorToken.Color,

    /* Surface */

    val surfacePrimary: ColorToken.Color,
    val onSurfacePrimary: ColorToken.Color,
    val surfaceSecondary: ColorToken.Color,
    val onSurfaceSecondary: ColorToken.Color,

    val surfaceDisabled: ColorToken.Color,
    val onSurfaceDisabled: ColorToken.Color,

    ) {
    internal companion object {
        fun darkMode(accentColor: ColorPrimitive = ColorPrimitive.CherryPop700) = ColorScheme(
            accent = ColorToken.Color("accent", accentColor),
            onAccent = ColorToken.Color("accent", accentColor.onColorPrimitive),
            shadow = ColorToken.Color("shadow", ColorPrimitive.Black800),
            textDisabled = ColorToken.Color("text-disabled", ColorPrimitive.Black400),
            textWarning = ColorToken.Color("text-warning", ColorPrimitive.CherryPop700),
            surfacePrimary = ColorToken.Color("surface-primary", ColorPrimitive.Purple500),
            onSurfacePrimary = ColorToken.Color("onsurface-primary", ColorPrimitive.White900),
            surfaceSecondary = ColorToken.Color("surface-secondary", ColorPrimitive.Purple700),
            onSurfaceSecondary = ColorToken.Color("onsurface-secondary", ColorPrimitive.White900),
            surfaceDisabled = ColorToken.Color("surface-disabled", ColorPrimitive.Black400),
            onSurfaceDisabled = ColorToken.Color("onsurface-disabled", ColorPrimitive.Black600),
            background = ColorToken.Color("background", ColorPrimitive.Purple900),
            onBackground = ColorToken.Color("onBackground", ColorPrimitive.White900),
            border = ColorToken.Color("border", ColorPrimitive.White900),
            text = ColorToken.Color("text", ColorPrimitive.White900),
            backgroundOverlay = ColorToken.Color("background-overlay", ColorPrimitive.Black900, alpha = 0.7f),
            borderDisabled = ColorToken.Color("border-disabled", ColorPrimitive.Black300),
            )
    }
}

internal val LocalColorScheme = compositionLocalOf<ColorScheme> {
    error("SpyfallTheme wasn't applied")
}

val LocalContentColor = compositionLocalOf<ColorToken.Color> {
    error("SpyfallTheme wasn't applied")
}

val LocalTypography = compositionLocalOf<Typography> {
    error("SpyfallTheme wasn't applied")
}

@Composable
fun ProvideContentColor(color: ColorToken.Color, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentColor provides color,
        androidx.compose.material3.LocalContentColor provides color.color,
        content = content
    )
}

// ///////// Previews \\\\\\\\\\\\\

private val ColorToken.designSystemName: String
    get() = when (this) {
        is ColorToken.Color -> primitive.designSystemName
        is ColorToken.Gradient -> primitive.designSystemName
    }

@Preview(device = "spec:shape=Normal,width=600,height=3000,unit=dp,dpi=150")
@Composable
private fun ColorTokenPreview(
    @PreviewParameter(ColorPreviewParameterProvider::class) parameter: ColorPreviewParameter,
) {
    @Composable
    fun PreviewCard(
        modifier: Modifier = Modifier,
    ) {
        PreviewCard(modifier = modifier) {
            parameter.colors.forEachIndexed { index, color ->
                if (index > 0) {
                    Spacer(Modifier.height(12.dp))
                }
                ColorRow(
                    name = {
                        Text(
                            color.tokenName,
                            style = OddOneOutTheme.typography.Heading.H700.style
                        )
                        when (val token = color(OddOneOutTheme.colorScheme)) {
                            is ColorToken.Color -> {
                                if (token.color.alpha < 1f) {
                                    Text(
                                        text = "alpha: ${token.color.alpha}",
                                        style = OddOneOutTheme.typography.Label.L500.style
                                    )
                                }
                            }
                            else -> {}
                        }

                    },
                    content = { ColorCard(color(OddOneOutTheme.colorScheme)) }
                )
            }
        }
    }

    PreviewContent(showBackground = true) {
        CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides LocalContentColor.current.color) {
            Column(Modifier.padding(Spacing.S500)) {
                Text(parameter.name, style = OddOneOutTheme.typography.Heading.H900.style)
                Spacer(Modifier.height(Spacing.S500))
                Row(Modifier) {
                    PreviewCard()
                }
            }
        }
    }
}

private data class ColorPreviewParameter(
    val name: String,
    val colors: List<KProperty1<ColorScheme, ColorToken>>,
)

private class ColorPreviewParameterProvider : PreviewParameterProvider<ColorPreviewParameter> {
    override val values: Sequence<ColorPreviewParameter>
        get() = sequenceOf(
            ColorPreviewParameter(
                name = "Background",
                colors = listOf(
                    ColorScheme::background,
                    ColorScheme::onBackground,
                    ColorScheme::backgroundOverlay
                )
            ),
            ColorPreviewParameter(
                name = "Text",
                colors = listOf(
                    ColorScheme::text,
                    ColorScheme::textWarning,
                    ColorScheme::textDisabled
                )
            ),
            ColorPreviewParameter(
                name = "Surface",
                colors = listOf(
                    ColorScheme::surfacePrimary,
                    ColorScheme::onSurfacePrimary,
                    ColorScheme::surfaceSecondary,
                    ColorScheme::onSurfaceSecondary,
                    ColorScheme::surfaceDisabled,
                    ColorScheme::onSurfaceDisabled,
                )
            ),
            ColorPreviewParameter(
                name = "Other",
                colors = listOf(
                    ColorScheme::accent,
                    ColorScheme::onAccent,
                    ColorScheme::border,
                    ColorScheme::borderDisabled
                )
            ),
        )
}

@Composable
private fun PreviewCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    OddOneOutTheme() {
        CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides LocalContentColor.current.color) {
            Column(
                modifier
                    .background(
                        color = ColorPrimitive.Black800.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(40.dp)
            ) {
                content()
            }
        }
    }
}

private val KProperty1<ColorScheme, ColorToken>.tokenName: String
    @ReadOnlyComposable
    @Composable
    get() = get(OddOneOutTheme.colorScheme).name

@Composable
private fun ColorRow(
    name: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(modifier) {
        Column(Modifier.weight(1f)) {
            Divider(color = (if (OddOneOutTheme.isDarkMode) ColorPrimitive.Black700 else ColorPrimitive.Black400).color)
            Spacer(Modifier.height(12.dp))
            ProvideTextStyle(OddOneOutTheme.typography.Heading.H700.style, name)
        }
        Spacer(Modifier.width(24.dp))
        ProvideTextStyle(OddOneOutTheme.typography.Heading.H500.style, content)
    }
}

@Composable
private fun ColorCard(
    token: ColorToken,
    modifier: Modifier = Modifier,
) {
    @Composable
    fun GradientColor(name: String, color: Color) {
        Column {
            Divider(color = (if (OddOneOutTheme.isDarkMode) ColorPrimitive.Black700 else ColorPrimitive.Black300).color)
            Spacer(Modifier.height(4.dp))
            Text(name)
            Text(color.toHexString())
        }
    }

    ColorCard(
        modifier = modifier,
        name = { Text(token.designSystemName) },
        details = {
            when (token) {
                is ColorToken.Color -> Text(token.color.toHexString())
                is ColorToken.Gradient -> {
                    Column {
                        GradientColor(name = "Color 1", color = token.from)
                        GradientColor(name = "Color 2", color = token.to)
                    }
                }
            }
        },
        showBorder = run {
            val primaryColor = when (token) {
                is ColorToken.Color -> token.color
                is ColorToken.Gradient -> token.from
            }
            if (OddOneOutTheme.isDarkMode) {
                primaryColor.luminance() < 0.03f
            } else {
                primaryColor.luminance() > 0.7f
            }
        },
        background = token.brush,
        textColor = when (token) {
            is ColorToken.Color -> token.primitive.onColorPrimitive.color
            is ColorToken.Gradient -> token.primitive.colorOn
        }
    )
}

@Composable
private fun ColorCard(
    name: @Composable () -> Unit,
    details: @Composable () -> Unit,
    showBorder: Boolean,
    background: Brush,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    ProvideTextStyle(TextStyle(color = textColor)) {
        Column(
            modifier = modifier
                .size(150.dp, 140.dp)
                .background(background, shape)
                .thenIf(showBorder) {
                    border(
                        1.dp,
                        (if (OddOneOutTheme.isDarkMode) ColorPrimitive.Black600 else ColorPrimitive.Black400).color,
                        shape
                    )
                }
                .padding(12.dp)
        ) {
            name()
            Spacer(Modifier.weight(1f))
            details()
        }
    }
}
