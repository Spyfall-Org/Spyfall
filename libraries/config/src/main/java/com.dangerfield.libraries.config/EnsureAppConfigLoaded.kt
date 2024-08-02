package com.dangerfield.libraries.config

import kotlinx.coroutines.flow.first
import oddoneout.core.Catching
import oddoneout.core.ignoreValue
import oddoneout.core.throwIfDebug
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ensure that the app config is loaded before the app starts
 */
@Singleton
class EnsureAppConfigLoaded @Inject constructor(
    private val appConfigFlow: AppConfigFlow
) {
    suspend operator fun invoke(): Catching<Unit> =
        Catching {
            appConfigFlow.first()
        }
            .throwIfDebug()
            .ignoreValue()
}