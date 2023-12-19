package com.dangerfield.libraries.config

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppConfigFlow (
    private val appConfigRepository: AppConfigRepository,
    private val appConfigFlow: Flow<AppConfigMap> = appConfigRepository.configStream()
): Flow<AppConfigMap> by appConfigFlow