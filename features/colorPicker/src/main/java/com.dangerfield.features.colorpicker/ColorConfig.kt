package com.dangerfield.features.colorpicker

import spyfallx.coreui.color.ColorPrimitive

sealed class ColorConfig {
    data object Random: ColorConfig()
    data class Specific(val colorPrimitive: ColorPrimitive): ColorConfig()
}
