package com.dangerfield.libraries.config

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * We cannot directly add a Flow<X> to the dependency graph, this is a workaround to provide a
 * way to listen to config changes easily.
 */
class AppConfigFlow (
    private val appConfigRepository: AppConfigRepository,
    private val appConfigFlow: Flow<AppConfigMap> = appConfigRepository.configStream()
): Flow<AppConfigMap> by appConfigFlow