package com.dangerfield.libraries.session

data class User(
    val id: String?,
    val languageCode: String,
    val themeConfig: ThemeConfig,
    val stats: Stats
)

