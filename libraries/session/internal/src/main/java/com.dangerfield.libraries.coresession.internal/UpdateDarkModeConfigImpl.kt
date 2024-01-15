package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.session.UpdateDarkModeConfig
import com.dangerfield.libraries.session.UserRepository
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UpdateDarkModeConfigImpl @Inject constructor(
    private val userRepository: UserRepository
): UpdateDarkModeConfig {

    override suspend fun invoke(darkModeConfig: DarkModeConfig) {
        userRepository.updateDarkModeConfig(darkModeConfig)
    }
}