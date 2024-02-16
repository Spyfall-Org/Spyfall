package com.dangerfield.spyfall.startup

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.ConsentStatusRepository
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
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
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.spyfall.startup.MainActivityViewModel.Action
import com.dangerfield.spyfall.startup.MainActivityViewModel.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import oddoneout.core.Try
import oddoneout.core.doNothing
import oddoneout.core.failFast
import oddoneout.core.logOnError
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
    private val isInMaintenanceMode: IsInMaintenanceMode
) : SEAViewModel<State, Unit, Action>() {

    override val initialState = State(
        isUpdateRequired = false,
        accentColor = ThemeColor.entries.random().colorPrimitive,
        isBlockingLoad = true,
        hasBlockingError = false,
        languageSupportLevelMessage = null,
        consentStatus = null,
        isInMaintenanceMode = false
    )

    init {
        takeAction(Action.LoadApp)
    }

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadApp -> loadApp()
            is Action.MarkLanguageSupportLevelMessageShown -> languageSupportMessageShown(action.languageSupportLevel)
            is Action.LoadConsentStatus -> listenForConsentStatusUpdates(action)
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
            .logOnError()
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

    private fun getSessionColorPrimitive(themeConfig: ThemeConfig): ColorPrimitive {
        val initialColorConfig = themeConfig.colorConfig
        val colorPrimitive = if (initialColorConfig is ColorConfig.Specific) {
            initialColorConfig.color.colorPrimitive
        } else {
            ThemeColor.entries.random().colorPrimitive
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

    private val CoroutineScope.requiredStartupTasks: List<Deferred<Try<Unit>>>
        get() = listOf(
            async { ensureSessionLoaded() },
            async { ensureAppConfigLoaded() }
        )

    sealed class Action {
        data object LoadApp : Action()
        data class LoadConsentStatus(val activity: Activity) : Action()
        data class MarkLanguageSupportLevelMessageShown(val languageSupportLevel: LanguageSupportLevel) :
            Action()
    }

    data class State(
        val isBlockingLoad: Boolean,
        val isUpdateRequired: Boolean,
        val accentColor: ColorPrimitive,
        val hasBlockingError: Boolean,
        val consentStatus: ConsentStatus?,
        val isInMaintenanceMode: Boolean,
        val languageSupportLevelMessage: LanguageSupportLevelMessage?
    )

    data class LanguageSupportLevelMessage(
        val languageSupportLevel: LanguageSupportLevel
    )
}
