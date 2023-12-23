package com.dangerfield.libraries.ui

import androidx.compose.foundation.Indication
import androidx.compose.material.ripple.rememberRipple as RememberMaterialRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun rememberRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified
): Indication {
    return RememberMaterialRipple(
        bounded = bounded,
        radius = radius,
        color = color
    )
}
