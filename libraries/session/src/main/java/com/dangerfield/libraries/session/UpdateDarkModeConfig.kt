package com.dangerfield.libraries.session

interface UpdateDarkModeConfig {
    suspend operator fun invoke(darkModeConfig: DarkModeConfig)
}