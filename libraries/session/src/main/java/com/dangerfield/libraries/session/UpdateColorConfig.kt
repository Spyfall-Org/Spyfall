package com.dangerfield.libraries.session


interface UpdateColorConfig {
    suspend operator fun invoke(config: ColorConfig)
}