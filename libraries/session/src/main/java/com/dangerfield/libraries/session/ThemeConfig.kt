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

enum class DarkModeConfig {
    System, Dark, Light
}