package spyfallx.coreui.theme


import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.color.ColorScheme
import spyfallx.coreui.color.ColorToken
import spyfallx.coreui.color.LocalColorScheme
import spyfallx.coreui.color.LocalContentColor
import spyfallx.coreui.color.LocalTypography

//TODO hide colors & typography into the internal module so that people must go through the theme
object SpyfallTheme {
    val isDarkMode: Boolean
        @ReadOnlyComposable
        @Composable
        get() = colorScheme === ColorScheme.darkMode()

    val colorScheme: ColorScheme
        @ReadOnlyComposable
        @Composable
        get() = LocalColorScheme.current

    val typography: spyfallx.coreui.typography.Typography
        @ReadOnlyComposable
        @Composable
        get() = LocalTypography.current

}

@Composable
fun SpyfallTheme(
    accentColor: ColorPrimitive = ColorPrimitive.CherryPop700,
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkMode) {
        ColorScheme.darkMode(accentColor)
    } else {
        ColorScheme.lightMode(accentColor)
    }

    SetupMaterialTheme {
        CompositionLocalProvider(
            LocalColorScheme provides colorScheme,
            LocalContentColor provides colorScheme.textPrimary,
            LocalTypography provides spyfallx.coreui.typography.Typography,
            LocalMinimumInteractiveComponentEnforcement provides false,
            androidx.compose.material3.LocalContentColor provides Color.Magenta,
            content = content
        )
    }
}

/**
 * Sets up an invalid material theme to clearly highlight any usages of the material colors and typography that doesn't
 * follow the design system.
 *
 * Any time a material component is used, colors and typography should be explicitly provided instead of relying on the
 * theme.
 */
@Composable
private fun SetupMaterialTheme(content: @Composable () -> Unit) {
    val invalidTextStyle = TextStyle(color = Color.Magenta)
    MaterialTheme(
        typography = Typography(
            displayLarge = invalidTextStyle,
            displayMedium = invalidTextStyle,
            displaySmall = invalidTextStyle,
            headlineLarge = invalidTextStyle,
            headlineMedium = invalidTextStyle,
            headlineSmall = invalidTextStyle,
            titleLarge = invalidTextStyle,
            titleMedium = invalidTextStyle,
            titleSmall = invalidTextStyle,
            bodyLarge = invalidTextStyle,
            bodyMedium = invalidTextStyle,
            bodySmall = invalidTextStyle,
            labelLarge = invalidTextStyle,
            labelMedium = invalidTextStyle,
            labelSmall = invalidTextStyle
        ),
        colorScheme = androidx.compose.material3.ColorScheme(
            primary = Color.Magenta,
            onPrimary = Color.Magenta,
            primaryContainer = Color.Magenta,
            onPrimaryContainer = Color.Magenta,
            inversePrimary = Color.Magenta,
            secondary = Color.Magenta,
            onSecondary = Color.Magenta,
            secondaryContainer = Color.Magenta,
            onSecondaryContainer = Color.Magenta,
            tertiary = Color.Magenta,
            onTertiary = Color.Magenta,
            tertiaryContainer = Color.Magenta,
            onTertiaryContainer = Color.Magenta,
            background = Color.Magenta,
            onBackground = Color.Magenta,
            surface = Color.Magenta,
            onSurface = Color.Magenta,
            surfaceVariant = Color.Magenta,
            onSurfaceVariant = Color.Magenta,
            surfaceTint = Color.Magenta,
            inverseSurface = Color.Magenta,
            inverseOnSurface = Color.Magenta,
            error = Color.Magenta,
            onError = Color.Magenta,
            errorContainer = Color.Magenta,
            onErrorContainer = Color.Magenta,
            outline = Color.Magenta,
            outlineVariant = Color.Magenta,
            scrim = Color.Magenta
        ),
        content = content
    )
}
