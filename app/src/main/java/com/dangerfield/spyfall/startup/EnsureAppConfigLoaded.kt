package com.dangerfield.spyfall.startup

import com.dangerfield.libraries.config.AppConfigRepository
import kotlinx.coroutines.flow.first
import oddoneout.core.Catching
import oddoneout.core.ignoreValue
import oddoneout.core.throwIfDebug
import javax.inject.Inject
import javax.inject.Singleton

// TODO cleanup feels like this should be in the app config library
@Singleton
class EnsureAppConfigLoaded @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) {
    suspend operator fun invoke(): Catching<Unit> =
        Catching {
            appConfigRepository.configStream().first()
        }
            .throwIfDebug()
            .ignoreValue()
}