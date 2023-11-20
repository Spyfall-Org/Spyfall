package com.dangerfield.spyfall.startup

import com.dangerfield.libraries.config.internal.AppConfigRepository
import kotlinx.coroutines.flow.first
import spyfallx.core.Try
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureAppConfigLoaded @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) {
    suspend operator fun invoke(): Try<Unit> =
        Try {
            appConfigRepository.configStream().first()
        }.ignoreValue()
}