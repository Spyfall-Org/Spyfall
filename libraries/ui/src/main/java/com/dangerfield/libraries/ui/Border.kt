package com.dangerfield.libraries.ui

import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import spyfallx.ui.color.ColorToken
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Immutable
data class Border(val color: ColorToken.Color, val width: Dp = StandardBorderWidth) {
    companion object {
        val Standard: Border
            @ReadOnlyComposable
            @Composable
            get() = Border(OddOneOutTheme.colorScheme.border)
    }
}

val StandardBorderWidth = 1.dp

fun Border.toBorderStroke(): BorderStroke =
    BorderStroke(width, color.brush)
