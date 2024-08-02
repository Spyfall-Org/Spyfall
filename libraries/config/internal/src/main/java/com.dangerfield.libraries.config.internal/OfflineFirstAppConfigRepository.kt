package com.dangerfield.libraries.config.internal

import androidx.annotation.VisibleForTesting
import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfigOverrideRepository
import com.dangerfield.libraries.config.internal.model.FallbackConfigMapMapBased
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.childSupervisorScope
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import se.ansman.dagger.auto.AutoBind
import se.ansman.dagger.auto.AutoInitialize
import oddoneout.core.ApplicationState
import oddoneout.core.ApplicationStateRepository
import oddoneout.core.showDebugSnack
import oddoneout.core.doNothing
import oddoneout.core.ignoreValue
import oddoneout.core.withBackoffRetry
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@VisibleForTesting
val ConfigRefreshRate = 15.minutes
private val ConfigRefreshTimeout = 10.seconds

/**
 * Repository responsible for exposing the app config and a method to refresh it.
 * Starts refreshing the app config on a set cadence (short polling) upon app start
 * and stops when the app lifecycle stops.
 *
 * internally backed by datastore, allowing for offline support.
 *
 * NOTE:
 * The interface is not in the api because this class should not be injected for most usecases.
 * Rather prefer to inject the app config or the flow of app config.
 */
@Singleton
@AutoBind
@AutoInitialize
class OfflineFirstAppConfigRepository @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val configDataSource: ConfigDataSource,
    private val cachedConfigDataSource: CachedConfigDataSource,
    private val fallbackConfig: FallbackConfigMapMapBased,
    private val configOverrideRepository: ConfigOverrideRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    applicationStateRepository: ApplicationStateRepository,
) : AppConfigRepository {

    private var refreshPollingJob: Job? = null
    private var refreshJob: Job? = null

    init {
        Timber.d("Initializing AppConfigRepository")
        applicationStateRepository.applicationState()
            .onEach { state ->
                when (state) {
                    ApplicationState.Foregrounded -> startAppConfigRefresh()
                    ApplicationState.Backgrounded -> stopAppConfigRefresh()
                    ApplicationState.Destroyed -> doNothing()
                }
            }.launchIn(applicationScope)
    }

    private val configStream: SharedFlow<AppConfigMap> = flow {
        withContext(dispatcherProvider.io) {
            refreshConfig().join() // before emitting whats cached, make sure its up to date
        }
        val configFlow = combine(
            configOverrideRepository.getOverridesFlow(),
            cachedConfigDataSource.getConfigFlow()
        ) { overrides, config ->
            config.applyOverrides(overrides)
        }

        // TODO apply targeted overrides (starting with version overrides)
        emitAll(configFlow)
    }
        .distinctUntilChanged()
        .onEach {
            Timber.d("Config emitted: ${it.map}")
        }
        .shareIn(
        scope = applicationScope,
        started = SharingStarted.Eagerly,
        replay = 1
    )

    /**
     * Supplies the app config value
     */
    override fun config(): AppConfigMap = LazyMapBasedAppConfigMapMap()

    /**
     * Supplies the app config stream, updates on a set cadence (short polling)
     */
    override fun configStream(): Flow<AppConfigMap> = configStream

    /**
     * starts refreshing the app config on an interval
     */
    private suspend fun startAppConfigRefresh() {
        if (refreshPollingJob?.isActive == true) return
        Timber.d("Starting Config Refresher")
        refreshPollingJob = applicationScope.childSupervisorScope(dispatcherProvider.io).launch {
            while (isActive) {
                Timber.d("Refreshing app config")
                refreshConfig().join()
                delay(ConfigRefreshRate)
            }
        }
    }

    /**
     * stops refreshing the app config
     */
    private fun stopAppConfigRefresh() {
        Timber.d("Stopping Config Refresher")
        refreshPollingJob?.cancel()
        refreshPollingJob = null
    }

    /**
     * return the job responsible for refreshing the app config stored in datastore from the backend
     * if fails, will use the cached config if available, otherwise will use the fallback config
     */
    private suspend fun refreshConfig(): Job {
        val immutableRefreshJob = refreshJob
        return if (immutableRefreshJob != null && immutableRefreshJob.isActive) {
            immutableRefreshJob
        } else {
            applicationScope.childSupervisorScope(dispatcherProvider.io).launch {
                tryWithTimeout(ConfigRefreshTimeout) {
                    withBackoffRetry(2) {
                        Timber.d("Refreshing app config. Attempt: $it")
                        configDataSource.getConfig()
                    }
                }
                    .onSuccess { config ->
                        cachedConfigDataSource.updateConfig(config)
                    }.onFailure { throwable ->
                        if (cachedConfigDataSource.getConfig().isFailure) {
                            showDebugSnack {
                                "Failed to refresh app config, None Cached. Using Fallback."
                            }
                            Timber.d(
                                throwable,
                                "Failed to refresh app config, no cached config, using fallback"
                            )
                            cachedConfigDataSource.updateConfig(fallbackConfig)
                        } else {
                            showDebugSnack {
                                "Failed to refresh app config, using cached config."
                            }
                            Timber.d(throwable, "Failed to refresh app config, using cached config")
                        }
                    }.ignoreValue()
            }.also { refreshJob = it }
        }
    }

    /**
     * Implementation that will lazily load the [Map]. The [Map] will not be retrieved until [value] gets invoked.
     * This allows [AppConfigMap] to be injected anywhere without the need to have the data loaded.
     * If the [configStream] already has a value, that will be returned directly. otherwise
     * the value will be retrieved from the stream.
     */
    inner class LazyMapBasedAppConfigMapMap : AppConfigMap() {

        override val map: Map<String, *>
            get() = configStream.replayCache.firstOrNull()?.map ?: runBlocking {
                configStream.first().map
            }
    }
}
