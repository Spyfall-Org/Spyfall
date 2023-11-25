package com.dangerfield.features.colorpicker

import com.dangerfield.libraries.ui.color.ColorPrimitive

sealed class ColorConfig {
    data object Random: ColorConfig()
    data class Specific(val colorPrimitive: ColorPrimitive): ColorConfig()
}
