package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.session.UpdateDarkModeConfig
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.doNothing
import javax.inject.Inject

@AutoBind
class UpdateDarkModeConfigImpl @Inject constructor(): UpdateDarkModeConfig {

    override suspend fun invoke(darkModeConfig: DarkModeConfig) {
        doNothing()
    }
}