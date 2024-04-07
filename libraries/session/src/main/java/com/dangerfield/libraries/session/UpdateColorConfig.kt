package com.dangerfield.libraries.session

/**
 * updates the users color choices
 */
interface UpdateColorConfig {
    suspend operator fun invoke(config: ColorConfig)
}