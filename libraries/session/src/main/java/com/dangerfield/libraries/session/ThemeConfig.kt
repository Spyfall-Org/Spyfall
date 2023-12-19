package com.dangerfield.libraries.session

import com.dangerfield.libraries.ui.color.ThemeColor

data class ThemeConfig(
    val colorConfig: ColorConfig,
    val darkModeConfig: DarkModeConfig
)

sealed class ColorConfig {
    data object Random : ColorConfig()
    data class Specific(val color: ThemeColor) : ColorConfig()
}

sealed class DarkModeConfig {
    data object System : DarkModeConfig()
    data object Dark : DarkModeConfig()
    data object Light : DarkModeConfig()
}