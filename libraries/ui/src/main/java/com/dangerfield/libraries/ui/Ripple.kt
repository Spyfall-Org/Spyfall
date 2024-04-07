package com.dangerfield.libraries.ui

import androidx.compose.foundation.Indication
import androidx.compose.material.ripple.rememberRipple as rememberMaterialRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun rememberRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.White.copy(alpha = 0.2f)
): Indication {
    return rememberMaterialRipple(
        bounded = bounded,
        radius = radius,
        color = color
    )
}
