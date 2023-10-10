package spyfallx.coreui.color

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
import spyfallx.coreui.Spacing
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.theme.SpyfallTheme
import spyfallx.coreui.thenIf
import spyfallx.coreui.typography.Typography
import kotlin.reflect.KProperty1

@Immutable
class ColorScheme internal constructor(

    val accentColor: ColorToken.Color,

    /* Backgrounds */

    val shadow: ColorToken.Color,

    val backgroundGradient: ColorToken.Gradient,

    val backgroundPrimary: ColorToken.Color,

    val backgroundSecondary: ColorToken.Color,

    val backgroundAccent: ColorToken.Color,

    val backgroundOverlay: ColorToken.Color,

    val borderPrimary: ColorToken.Color,

    /* Texts */

    val textPrimary: ColorToken.Color,

    val textAccentPrimary: ColorToken.Color,

    val textSecondary: ColorToken.Color,

    val textTertiary: ColorToken.Color,

    val textDisabled: ColorToken.Color,

    val textWarning: ColorToken.Color,

    /* Surface */

    val surfacePrimary: ColorToken.Color,

    val onSurfacePrimary: ColorToken.Color,

    val surfaceSecondary: ColorToken.Color,

    val onSurfaceSecondary: ColorToken.Color,

    val surfaceAccentPrimary: ColorToken.Color,

    val surfaceAccentSecondary: ColorToken.Color,

    val surfaceTertiary: ColorToken.Color,

    val onSurfaceTertiary: ColorToken.Color,

    val surfaceDisabled: ColorToken.Color,

    val onSurfaceDisabled: ColorToken.Color,

    val surfacePositive: ColorToken.Color,

    val surfaceRecent: ColorToken.Color,

    val surfaceNegative: ColorToken.Color,

    val surfaceWarning: ColorToken.Color,
) {
    internal companion object {
        fun lightMode(accentColor: ColorPrimitive = ColorPrimitive.CherryPop700) = ColorScheme(
            accentColor = ColorToken.Color("accent", accentColor),
            shadow = ColorToken.Color("shadow", ColorPrimitive.Black800),
            backgroundGradient = ColorToken.Gradient("background-gradient", ColorGradientPrimitive.Light),
            backgroundPrimary = ColorToken.Color("background-primary", ColorPrimitive.White900),
            backgroundSecondary = ColorToken.Color("background-secondary", ColorPrimitive.Black100),
            backgroundAccent = ColorToken.Color("background-accent", ColorPrimitive.GrapeJelly500),
            backgroundOverlay = ColorToken.Color("background-overlay", ColorPrimitive.Black300, alpha = 0.8f),
            borderPrimary = ColorToken.Color("border-primary", ColorPrimitive.Black200),
            textPrimary = ColorToken.Color("text-primary", ColorPrimitive.Black900),
            textAccentPrimary = ColorToken.Color("text-accent-primary", ColorPrimitive.MintyFresh300),
            textSecondary = ColorToken.Color("text-secondary", ColorPrimitive.Black700),
            textTertiary = ColorToken.Color("text-tertiary", ColorPrimitive.Black500),
            textDisabled = ColorToken.Color("text-disabled", ColorPrimitive.Black400),
            textWarning = ColorToken.Color("text-warning", ColorPrimitive.CherryPop700),
            surfacePrimary = ColorToken.Color("surface-primary", ColorPrimitive.Black200),
            onSurfacePrimary = ColorToken.Color("onsurface-primary", ColorPrimitive.Black900),
            surfaceSecondary = ColorToken.Color("surface-secondary", ColorPrimitive.Black300),
            onSurfaceSecondary = ColorToken.Color("onsurface-secondary", ColorPrimitive.Black900),
            surfaceAccentPrimary = ColorToken.Color("surface-accent-primary", ColorPrimitive.GrapeJelly500),
            surfaceAccentSecondary = ColorToken.Color("surface-accent-secondary", ColorPrimitive.SkyDive400),
            surfaceTertiary = ColorToken.Color("surface-tertiary", ColorPrimitive.Black100),
            onSurfaceTertiary = ColorToken.Color("onsurface-tertiary", ColorPrimitive.Black900),
            surfaceDisabled = ColorToken.Color("surface-disabled", ColorPrimitive.Black200),
            onSurfaceDisabled = ColorToken.Color("onsurface-disabled", ColorPrimitive.Black600),
            surfacePositive = ColorToken.Color("surface-positive", ColorPrimitive.MintyFresh300),
            surfaceRecent = ColorToken.Color("surface-recent", ColorPrimitive.Black200),
            surfaceNegative = ColorToken.Color("surface-negative", ColorPrimitive.CherryPop700),
            surfaceWarning = ColorToken.Color("surface-warning", ColorPrimitive.TangerineTwist600)
        )

        fun darkMode(accentColor: ColorPrimitive = ColorPrimitive.CherryPop700) = ColorScheme(
            accentColor = ColorToken.Color("accent", accentColor),
            shadow = ColorToken.Color("shadow-inverse", ColorPrimitive.White900),
            backgroundGradient = ColorToken.Gradient("background-gradient-inverse", ColorGradientPrimitive.Dark),
            backgroundPrimary = ColorToken.Color("background-primary-inverse", ColorPrimitive.Black900),
            backgroundSecondary = ColorToken.Color("background-secondary-inverse", ColorPrimitive.Black800),
            backgroundAccent = ColorToken.Color("background-accent-inverse", ColorPrimitive.GrapeJelly500),
            backgroundOverlay = ColorToken.Color("background-overlay-inverse", ColorPrimitive.Black600, alpha = 0.8f),
            borderPrimary = ColorToken.Color("border-primary-inverse", ColorPrimitive.Black700),
            textPrimary = ColorToken.Color("text-primary-inverse", ColorPrimitive.White900),
            textAccentPrimary = ColorToken.Color("text-accent-primary-inverse", ColorPrimitive.MintyFresh300),
            textSecondary = ColorToken.Color("text-secondary-inverse", ColorPrimitive.Black300),
            textTertiary = ColorToken.Color("text-tertiary-inverse", ColorPrimitive.Black400),
            textDisabled = ColorToken.Color("text-disabled-inverse", ColorPrimitive.Black500),
            textWarning = ColorToken.Color("text-warning-inverse", ColorPrimitive.CherryPop700),
            surfacePrimary = ColorToken.Color("surface-primary-inverse", ColorPrimitive.Black800),
            onSurfacePrimary = ColorToken.Color("onsurface-primary-inverse", ColorPrimitive.White900),
            surfaceSecondary = ColorToken.Color("surface-secondary-inverse", ColorPrimitive.Black700),
            onSurfaceSecondary = ColorToken.Color("onsurface-secondary-inverse", ColorPrimitive.White900),
            surfaceAccentPrimary = ColorToken.Color("surface-accent-primary-inverse", ColorPrimitive.GrapeJelly500),
            surfaceAccentSecondary = ColorToken.Color("surface-accent-secondary-inverse", ColorPrimitive.SkyDive400),
            surfaceTertiary = ColorToken.Color("surface-tertiary-inverse", ColorPrimitive.Black900),
            onSurfaceTertiary = ColorToken.Color("onsurface-tertiary-inverse", ColorPrimitive.White900),
            surfaceDisabled = ColorToken.Color("surface-disabled-inverse", ColorPrimitive.Black800),
            onSurfaceDisabled = ColorToken.Color("onsurface-disabled-inverse", ColorPrimitive.Black600),
            surfacePositive = ColorToken.Color("surface-positive-inverse", ColorPrimitive.MintyFresh300),
            surfaceRecent = ColorToken.Color("surface-recent-inverse", ColorPrimitive.Black800),
            surfaceNegative = ColorToken.Color("surface-negative-inverse", ColorPrimitive.CherryPop700),
            surfaceWarning = ColorToken.Color("surface-warning-inverse", ColorPrimitive.TangerineTwist600)
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

@Preview(device = "spec:shape=Normal,width=1200,height=3000,unit=dp,dpi=150")
@Composable
private fun ColorTokenPreview(
    @PreviewParameter(ColorPreviewParameterProvider::class) parameter: ColorPreviewParameter,
) {
    @Composable
    fun PreviewCard(
        isDarkMode: Boolean,
        modifier: Modifier = Modifier,
    ) {
        PreviewCard(isDarkMode = isDarkMode, modifier = modifier) {
            parameter.colors.forEachIndexed { index, color ->
                if (index > 0) {
                    Spacer(Modifier.height(12.dp))
                }
                ColorRow(
                    name = { Text(color.tokenName, style = SpyfallTheme.typography.Heading.H700.style) },
                    content = { ColorCard(color(SpyfallTheme.colorScheme)) }
                )
            }
        }
    }

    PreviewContent(showBackground = true) {
        CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides LocalContentColor.current.color) {
            Column(Modifier.padding(Spacing.S500)) {
                Text(parameter.name, style = SpyfallTheme.typography.Heading.H900.style)
                Spacer(Modifier.height(Spacing.S500))
                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1f), propagateMinConstraints = true) {
                        PreviewCard(isDarkMode = false)
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(Modifier.weight(1f), propagateMinConstraints = true) {
                        PreviewCard(isDarkMode = true)
                    }
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
                    ColorScheme::backgroundGradient,
                    ColorScheme::backgroundPrimary,
                    ColorScheme::backgroundSecondary,
                    ColorScheme::backgroundAccent,
                    ColorScheme::backgroundOverlay,
                    ColorScheme::borderPrimary
                )
            ),
            ColorPreviewParameter(
                name = "Text",
                colors = listOf(
                    ColorScheme::textPrimary,
                    ColorScheme::textAccentPrimary,
                    ColorScheme::textSecondary,
                    ColorScheme::textTertiary,
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
                    ColorScheme::surfaceAccentPrimary,
                    ColorScheme::surfaceAccentSecondary,
                    ColorScheme::surfaceTertiary,
                    ColorScheme::surfaceDisabled,
                    ColorScheme::onSurfaceDisabled,
                    ColorScheme::surfacePositive,
                    ColorScheme::surfaceRecent,
                    ColorScheme::surfaceNegative,
                    ColorScheme::surfaceWarning
                )
            ),
            ColorPreviewParameter(
                name = "Other",
                colors = listOf(
                    ColorScheme::accentColor,
                )
            ),
        )
}

@Composable
private fun PreviewCard(
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    SpyfallTheme(isDarkMode = isDarkMode) {
        CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides LocalContentColor.current.color) {
            Column(
                modifier
                    .background(
                        color = (if (isDarkMode) ColorPrimitive.Black800 else ColorPrimitive.Black200).color,
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
    get() = get(SpyfallTheme.colorScheme).name

@Composable
private fun ColorRow(
    name: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(modifier) {
        Column(Modifier.weight(1f)) {
            Divider(color = (if (SpyfallTheme.isDarkMode) ColorPrimitive.Black700 else ColorPrimitive.Black400).color)
            Spacer(Modifier.height(12.dp))
            ProvideTextStyle(SpyfallTheme.typography.Heading.H700.style, name)
        }
        Spacer(Modifier.width(24.dp))
        ProvideTextStyle(SpyfallTheme.typography.Heading.H500.style, content)
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
            Divider(color = (if (SpyfallTheme.isDarkMode) ColorPrimitive.Black700 else ColorPrimitive.Black300).color)
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
            if (SpyfallTheme.isDarkMode) {
                primaryColor.luminance() < 0.03f
            } else {
                primaryColor.luminance() > 0.7f
            }
        },
        background = token.brush,
        textColor = when (token) {
            is ColorToken.Color -> token.primitive.colorOn
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
                    border(1.dp, (if (SpyfallTheme.isDarkMode) ColorPrimitive.Black600 else ColorPrimitive.Black400).color, shape)
                }
                .padding(12.dp)
        ) {
            name()
            Spacer(Modifier.weight(1f))
            details()
        }
    }
}
