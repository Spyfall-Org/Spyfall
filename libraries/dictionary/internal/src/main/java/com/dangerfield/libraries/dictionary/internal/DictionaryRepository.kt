package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.dangerfield.libraries.coreflowroutines.childSupervisorScope
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.dictionary.Dictionary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import oddoneout.core.ApplicationStateRepository
import oddoneout.core.BuildInfo
import oddoneout.core.ForegroundState
import oddoneout.core.Try
import oddoneout.core.logOnError
import oddoneout.core.withBackoffRetry
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@VisibleForTesting
val RefreshRate = 15.minutes
private val RefreshTimeout = 10.seconds

/**
 * Repository responsible for exposing the dictionary and a method to refresh it.
 * Starts refreshing on a set cadence (short polling) upon app start
 * and stops when the app lifecycle stops.
 *
 * internally backed by datastore, allowing for offline support.
 *
 * NOTE:
 * The interface is not in the public module because this class should not be injected for most usecases.
 * Rather prefer to inject the dictionary directly.
 */
@Singleton
class DictionaryRepository @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val overrideDictionaryDataSource: OverrideDictionaryDataSource,
    private val cachedOverrideDictionaryDataSource: CachedDictionaryDataSource,
    private val defaultDictionary: ResourceXmlDictionary,
    private val buildInfo: BuildInfo,
    @ApplicationContext private val applicationContext: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
    applicationStateRepository: ApplicationStateRepository,
) {
    private var refreshPollingJob: Job? = null
    private var refreshJob: Job? = null

    init {
        Timber.d("Initializing DictionaryRepository")
        applicationStateRepository.foregroundStateFlow()
            .onEach { state ->
                when (state) {
                    ForegroundState.FOREGROUND -> startRefreshing()
                    ForegroundState.BACKGROUND -> stopRefreshingOverrideDictionary()
                }
            }.launchIn(applicationScope)
    }

    private val appDictionaryStream: SharedFlow<AppDictionary> = flow {
        // before emitting, try to refresh the override dictionary
        refreshOverrideDictionary()
        if (cachedOverrideDictionaryDataSource.getDictionary().isFailure) {
            emit(null)
        } else {
            emitAll(cachedOverrideDictionaryDataSource.getDictionaryFlow())
        }
    }.map { overrideDictionary ->
        AppDictionary(
            defaultDictionary = defaultDictionary,
            overrideDictionary = overrideDictionary,
            buildInfo = buildInfo,
            context = applicationContext
        )
    }

        .distinctUntilChanged()
        .onEach {
            Timber.d("New Dictionary emitted")
        }
        .shareIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    /**
     * Supplies the dictionary value
     */
    fun dictionary(): Dictionary = LazyAppDictionary()

    /**
     * Supplies the dictionary stream which updates on a set cadence (short polling)
     */
    fun dictionaryStream(): Flow<Dictionary> = appDictionaryStream

    private suspend fun startRefreshing() {
        if (refreshPollingJob?.isActive == true) return
        Timber.d("Starting dictionary Refresher")
        refreshPollingJob = applicationScope.childSupervisorScope(dispatcherProvider.io).launch {
            while (isActive) {
                Try { refreshOverrideDictionary() }.logOnError()
                delay(RefreshRate)
            }
        }
    }

    private fun stopRefreshingOverrideDictionary() {
        Timber.d("Stopping Dictionary Override Refresher")
        refreshPollingJob?.cancel()
        refreshPollingJob = null
    }

    /**
     * Request a new dictionary from back end and caches it
     * Failure if cant fetch or cache
     */
    private suspend fun refreshOverrideDictionary(): Try<Unit> = Try {
        val immutableRefreshJob = refreshJob
        val currentRefreshJob = if (immutableRefreshJob != null && immutableRefreshJob.isActive) {
            immutableRefreshJob
        } else {
            applicationScope.childSupervisorScope(dispatcherProvider.io).launch {
                tryWithTimeout(RefreshTimeout) {
                    withBackoffRetry(2) {
                        Timber.d("Refreshing dictionary. Attempt: $it")
                        overrideDictionaryDataSource.getDictionary()
                    }
                }
                    .onSuccess { dictionary ->
                        Try {
                            cachedOverrideDictionaryDataSource.updateDictionary(dictionary)
                        }
                            .getOrThrow()
                    }
                    .getOrThrow()
            }.also { refreshJob = it }
        }

        currentRefreshJob.join()
    }

    /**
     * Dictionary implementation based on the application dictionary stream
     * This allows for the dictionary to be lazily initialized so that we can provide it before
     * the dictionary is ready
     *
     * The first call to getString() will block until the dictionary is ready
     */
    inner class LazyAppDictionary : Dictionary {
        private val appDictionary: AppDictionary
            get() = appDictionaryStream.replayCache.firstOrNull() ?: runBlocking {
                appDictionaryStream.first()
            }

        override fun getString(key: Int, args: Map<String,String>): String = appDictionary.getString(key, args)

        override fun getOptionalString(key: Int, args: Map<String,String>): String? = appDictionary.getOptionalString(key, args)
    }
}
