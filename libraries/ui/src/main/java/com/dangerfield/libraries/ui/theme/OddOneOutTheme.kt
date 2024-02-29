package com.dangerfield.libraries.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.dangerfield.libraries.ui.LocalColors
import com.dangerfield.libraries.ui.LocalContentColor
import com.dangerfield.libraries.ui.LocalTypography
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.Colors
import com.dangerfield.libraries.ui.typography.Typography

object OddOneOutTheme {
    val colors: Colors
        @ReadOnlyComposable
        @Composable
        get() = LocalColors.current

    val typography = Typography()
}

@Composable
fun OddOneOutTheme(
    themeColor: ColorResource = ColorResource.CherryPop700,
    content: @Composable () -> Unit
) {
    val colors = remember(themeColor) { Colors.getColors(themeColor) }

    val textSelectionColors = TextSelectionColors(
        handleColor = colors.accent.color,
        backgroundColor = colors.accent.color.copy(alpha = 0.4F)
    )

    MaterialWrapper {
        CompositionLocalProvider(
            LocalColors provides colors,
            LocalContentColor provides colors.text,
            LocalTextSelectionColors provides textSelectionColors,
            LocalTypography provides OddOneOutTheme.typography,
            LocalMinimumInteractiveComponentEnforcement provides false,
            androidx.compose.material3.LocalContentColor provides colors.text.color,
            content = content
        )
    }
}

@Composable
private fun MaterialWrapper(content: @Composable () -> Unit) {
    val invalidTextStyle = TextStyle(color = Color.Red)
    MaterialTheme(
        typography = androidx.compose.material3.Typography(
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
            primary = Color.Red,
            onPrimary = Color.Red,
            primaryContainer = Color.Red,
            onPrimaryContainer = Color.Red,
            inversePrimary = Color.Red,
            secondary = Color.Red,
            onSecondary = Color.Red,
            secondaryContainer = Color.Red,
            onSecondaryContainer = Color.Red,
            tertiary = Color.Red,
            onTertiary = Color.Red,
            tertiaryContainer = Color.Red,
            onTertiaryContainer = Color.Red,
            background = Color.Red,
            onBackground = Color.Red,
            surface = Color.Red,
            onSurface = Color.Red,
            surfaceVariant = Color.Red,
            onSurfaceVariant = Color.Red,
            surfaceTint = Color.Red,
            inverseSurface = Color.Red,
            inverseOnSurface = Color.Red,
            error = Color.Red,
            onError = Color.Red,
            errorContainer = Color.Red,
            onErrorContainer = Color.Red,
            outline = Color.Red,
            outlineVariant = Color.Red,
            scrim = Color.Red
        ),
        content = content
    )
}
