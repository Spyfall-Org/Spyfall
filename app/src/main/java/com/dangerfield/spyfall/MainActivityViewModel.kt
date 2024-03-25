package com.dangerfield.spyfall

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.ConsentStatusRepository
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import com.dangerfield.features.inAppMessaging.CompleteInAppUpdate
import com.dangerfield.features.inAppMessaging.GetInAppUpdateAvailability
import com.dangerfield.features.inAppMessaging.InAppUpdateAvailability
import com.dangerfield.features.inAppMessaging.StartInAppUpdate
import com.dangerfield.features.inAppMessaging.UpdateStatus
import com.dangerfield.libraries.config.AppConfigFlow
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.dictionary.GetDeviceLanguageSupportLevel
import com.dangerfield.libraries.dictionary.LanguageSupportLevel
import com.dangerfield.libraries.dictionary.LanguageSupportMessageShown
import com.dangerfield.libraries.dictionary.ShouldShowLanguageSupportMessage
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.EnsureSessionLoaded
import com.dangerfield.libraries.session.SessionFlow
import com.dangerfield.libraries.session.ThemeConfig
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.spyfall.MainActivityViewModel.Action
import com.dangerfield.spyfall.MainActivityViewModel.State
import com.dangerfield.spyfall.startup.EnsureAppConfigLoaded
import com.dangerfield.spyfall.startup.IsInMaintenanceMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import oddoneout.core.Catching
import oddoneout.core.doNothing
import oddoneout.core.failFast
import oddoneout.core.logOnFailure
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val ensureAppConfigLoaded: EnsureAppConfigLoaded,
    private val ensureSessionLoaded: EnsureSessionLoaded,
    private val isAppUpdateRequired: IsAppUpdateRequired,
    private val consentStatusRepository: ConsentStatusRepository,
    private val sessionFlow: SessionFlow,
    private val getLanguageSupportLevel: GetDeviceLanguageSupportLevel,
    private val shouldShowLanguageSupportMessage: ShouldShowLanguageSupportMessage,
    private val languageSupportMessageShown: LanguageSupportMessageShown,
    private val appConfigFlow: AppConfigFlow,
    private val isInMaintenanceMode: IsInMaintenanceMode,
    private val getInAppUpdateAvailability: GetInAppUpdateAvailability,
    private val startInAppUpdate: StartInAppUpdate,
    private val completeInAppUpdate: CompleteInAppUpdate
) : SEAViewModel<State, Unit, Action>() {

    // TODO make a notification manager to handle all these dialogs I may or may not show.
    // in app messages can likely have a handler that takes in a class wrapping the route and stuff
    // and a priority and such.
    private var inAppUpdateJob: Job? = null

    override val initialState = State(
        isUpdateRequired = false,
        accentColor = ThemeColor.entries.random().colorResource,
        isBlockingLoad = true,
        hasBlockingError = false,
        languageSupportLevelMessage = null,
        consentStatus = null,
        isInMaintenanceMode = false,
        inAppUpdateStatus = null
    )

    init {
        takeAction(Action.LoadApp)
    }

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadApp -> loadApp()
            is Action.MarkLanguageSupportLevelMessageShown -> languageSupportMessageShown(action.languageSupportLevel)
            is Action.LoadConsentStatus -> listenForConsentStatusUpdates(action)
            is Action.StartInAppUpdate -> launchInAppUpdate(action)
            Action.CompleteInAppUpdate -> handleInstall()
        }
    }

    fun startInAppUpdate(
        activity: Activity,
        updateStatus: UpdateStatus.UpdateAvailable
    ) {
        takeAction(Action.StartInAppUpdate(activity, updateStatus))
    }

    fun installUpdate() {
        takeAction(Action.CompleteInAppUpdate)
    }

    private suspend fun MainActivityViewModel.handleInstall() {
        updateState { it.copy(inAppUpdateStatus = UpdateStatus.Installing) }
        completeInAppUpdate()
            .onSuccess {
                updateState { it.copy(inAppUpdateStatus = UpdateStatus.Installed) }
            }
            .onFailure { error ->
                updateState { it.copy(inAppUpdateStatus = UpdateStatus.Failed(error)) }
            }
    }

    private fun launchInAppUpdate(action: Action.StartInAppUpdate) {
        if (inAppUpdateJob?.isActive == true) return
        inAppUpdateJob = viewModelScope.launch {
            startInAppUpdate(
                action.updateStatus.appUpdateInfo,
                action.updateStatus.isForegroundUpdate,
                action.activity
            ).collect { status ->
                updateState { it.copy(inAppUpdateStatus = status) }
            }
        }
    }

    private fun listenForConsentStatusUpdates(action: Action.LoadConsentStatus) {
        viewModelScope.launch {
            consentStatusRepository.getStatusFlow(action.activity)
                .collectLatest { status ->
                    updateState { it.copy(consentStatus = status) }
                }
        }
    }

    private suspend fun loadApp() {
        tryWithTimeout(10.seconds) {
            requiredStartupTasks.awaitAll().failFast()
        }
            .logOnFailure()
            .onFailure {
                updateState {
                    it.copy(
                        hasBlockingError = true,
                        isBlockingLoad = false
                    )
                }
            }
            .onSuccess {
                val session = sessionFlow.first()
                val colorPrimitive = getSessionColorPrimitive(session.user.themeConfig)

                updateState {
                    it.copy(
                        isBlockingLoad = false,
                        accentColor = colorPrimitive,
                        languageSupportLevelMessage = null
                    )
                }

                listenForAppUpdateRequired()
                listenForSessionUpdates()
                getLanguageSupport()
                listenForConfigUpdates()
                checkForAppUpdate()
            }
    }

    private suspend fun checkForAppUpdate() {
        viewModelScope.launch {
            getInAppUpdateAvailability()
                .logOnFailure()
                .onSuccess { availability ->
                    when (availability) {
                        is InAppUpdateAvailability.UpdateAvailable -> {
                            updateState {
                                it.copy(
                                    inAppUpdateStatus = UpdateStatus.UpdateAvailable(
                                        shouldUpdate = availability.shouldShow,
                                        appUpdateInfo = availability.appUpdateInfo,
                                        isForegroundUpdate = availability.isForegroundUpdate
                                    )
                                )
                            }
                        }

                        is InAppUpdateAvailability.NoUpdateAvailable -> {
                            updateState {
                                it.copy(inAppUpdateStatus = UpdateStatus.NoUpdateAvailable)
                            }
                        }

                        is InAppUpdateAvailability.UpdateReadyToInstall -> {
                            updateState {
                                it.copy(
                                    inAppUpdateStatus = UpdateStatus.Downloaded(
                                        wasBackgroundUpdate = availability.wasDownloadedInBackground
                                    )
                                )
                            }
                        }

                        InAppUpdateAvailability.UpdateInProgress -> {
                            // Should be an impossilbe state.
                            doNothing()
                        }
                    }
                }
        }
    }

    private suspend fun getLanguageSupport() {
        viewModelScope.launch {
            val languageSupportLevel = getLanguageSupportLevel()
            val shouldShow = shouldShowLanguageSupportMessage(languageSupportLevel)
            if (shouldShow) {
                updateState {
                    it.copy(
                        languageSupportLevelMessage = LanguageSupportLevelMessage(
                            languageSupportLevel
                        )
                    )
                }
            }
        }
    }

    private suspend fun listenForSessionUpdates() {
        viewModelScope.launch {
            sessionFlow
                .map { it.user.themeConfig }
                .distinctUntilChanged()
                .collectLatest { config ->
                    val colorPrimitive = getSessionColorPrimitive(config)
                    updateState { state ->
                        state.copy(
                            accentColor = colorPrimitive,
                        )
                    }
                }
        }
    }

    private suspend fun listenForConfigUpdates() {
        viewModelScope.launch {
            appConfigFlow
                .distinctUntilChanged()
                .collectLatest { _ ->
                    updateState { state ->
                        state.copy(
                            isInMaintenanceMode = isInMaintenanceMode(),
                        )
                    }
                }
        }
    }

    private fun getSessionColorPrimitive(themeConfig: ThemeConfig): ColorResource {
        val initialColorConfig = themeConfig.colorConfig
        val colorPrimitive = if (initialColorConfig is ColorConfig.Specific) {
            initialColorConfig.color.colorResource
        } else {
            ThemeColor.entries.random().colorResource
        }
        return colorPrimitive
    }

    private fun listenForAppUpdateRequired() {
        viewModelScope.launch {
            isAppUpdateRequired()
                .distinctUntilChanged()
                .collectLatest { isUpdateRequired ->
                    updateState {
                        it.copy(isUpdateRequired = isUpdateRequired)
                    }
                }
        }
    }

    private val CoroutineScope.requiredStartupTasks: List<Deferred<Catching<Unit>>>
        get() = listOf(
            async { ensureSessionLoaded() },
            async { ensureAppConfigLoaded() }
        )

    sealed class Action {
        data object LoadApp : Action()
        data class LoadConsentStatus(val activity: Activity) : Action()
        data class StartInAppUpdate(
            val activity: Activity,
            val updateStatus: UpdateStatus.UpdateAvailable
        ) : Action()

        data object CompleteInAppUpdate : Action()

        data class MarkLanguageSupportLevelMessageShown(val languageSupportLevel: LanguageSupportLevel) :
            Action()
    }

    data class State(
        val isBlockingLoad: Boolean,
        val isUpdateRequired: Boolean,
        val accentColor: ColorResource,
        val hasBlockingError: Boolean,
        val consentStatus: ConsentStatus?,
        val isInMaintenanceMode: Boolean,
        val inAppUpdateStatus: UpdateStatus?,
        val languageSupportLevelMessage: LanguageSupportLevelMessage?
    )

    data class LanguageSupportLevelMessage(
        val languageSupportLevel: LanguageSupportLevel
    )
}
