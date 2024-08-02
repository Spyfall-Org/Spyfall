package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.AppConfigFlow
import com.dangerfield.libraries.config.AppConfigMap
import kotlinx.coroutines.flow.FlowCollector
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

/**
 * We cannot directly add a Flow<X> to the dependency graph, this is a workaround to provide a
 * way to listen to config changes easily.
 */
@AutoBind
class AppConfigFlowImpl @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
): AppConfigFlow {

    override suspend fun collect(collector: FlowCollector<AppConfigMap>) {
        appConfigRepository.configStream().collect(collector)
    }
}