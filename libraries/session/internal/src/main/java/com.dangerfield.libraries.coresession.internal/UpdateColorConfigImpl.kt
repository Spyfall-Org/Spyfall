package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.UpdateColorConfig
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.doNothing
import javax.inject.Inject

@AutoBind
class UpdateColorConfigImpl @Inject constructor(
    private val userRepository: UserRepository
) : UpdateColorConfig {
    override suspend fun invoke(config: ColorConfig) {
        userRepository.updateUser {
            copy(themeConfig = themeConfig.copy(colorConfig = config))
        }
    }
}