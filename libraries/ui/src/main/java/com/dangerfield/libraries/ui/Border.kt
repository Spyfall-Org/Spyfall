package com.dangerfield.libraries.ui

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Immutable
data class Border(val color: ColorResource, val width: Dp = StandardBorderWidth) {
    companion object {
        val Standard: Border
            @ReadOnlyComposable
            @Composable
            get() = Border(OddOneOutTheme.colors.border)
    }
}

val StandardBorderWidth = 1.dp

fun Modifier.border(border: Border): Modifier = this.border(
    width = border.width,
    color = border.color.color
)