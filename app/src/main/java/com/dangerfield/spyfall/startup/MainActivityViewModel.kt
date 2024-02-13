package com.dangerfield.spyfall.startup

import androidx.lifecycle.viewModelScope
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import com.dangerfield.features.termOfService.GetLegalAcceptanceState
import com.dangerfield.features.termOfService.LegalAcceptanceState
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
import oddoneout.core.failFast
import oddoneout.core.logOnError
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val ensureAppConfigLoaded: EnsureAppConfigLoaded,
    private val ensureSessionLoaded: EnsureSessionLoaded,
    private val isAppUpdateRequired: IsAppUpdateRequired,
    private val getLegalAcceptanceState: GetLegalAcceptanceState,
    private val sessionFlow: SessionFlow,
    private val getLanguageSupportLevel: GetDeviceLanguageSupportLevel,
    private val shouldShowLanguageSupportMessage: ShouldShowLanguageSupportMessage,
    private val languageSupportMessageShown: LanguageSupportMessageShown,
) : SEAViewModel<State, Unit, Action>() {

    override val initialState = State(
        isUpdateRequired = false,
        accentColor = ThemeColor.entries.random().colorPrimitive,
        isBlockingLoad = true,
        hasBlockingError = false,
        languageSupportLevelMessage = null,
        legalAcceptanceState = null
    )

    init {
        takeAction(Action.LoadApp)
    }

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadApp -> loadApp()
            is Action.MarkLanguageSupportLevelMessageShown -> languageSupportMessageShown(action.languageSupportLevel)
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

                viewModelScope.launch {
                    listenForAppUpdateRequired()
                }

                viewModelScope.launch {
                    listenForConfigUpdates()
                }

                viewModelScope.launch {
                    getLanguageSupport()
                }

                viewModelScope.launch {
                    listenForLegalAcceptanceState()
                }
            }
    }

    private suspend fun getLanguageSupport() {
        val languageSupportLevel = getLanguageSupportLevel()
        val shouldShow = shouldShowLanguageSupportMessage(languageSupportLevel)
        if (shouldShow) {
            updateState {
                it.copy(languageSupportLevelMessage = LanguageSupportLevelMessage(languageSupportLevel))
            }
        }
    }

    private suspend fun listenForConfigUpdates() {
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

    private suspend fun listenForLegalAcceptanceState() {
        getLegalAcceptanceState()
            .distinctUntilChanged()
            .collectLatest { legalAcceptanceState ->
                updateState { state ->
                    state.copy(
                       legalAcceptanceState = legalAcceptanceState
                    )
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
        data class MarkLanguageSupportLevelMessageShown(val languageSupportLevel: LanguageSupportLevel) : Action()
    }

    data class State(
        val isBlockingLoad: Boolean,
        val isUpdateRequired: Boolean,
        val accentColor: ColorPrimitive,
        val hasBlockingError: Boolean,
        val legalAcceptanceState: LegalAcceptanceState?,
        val languageSupportLevelMessage: LanguageSupportLevelMessage?
    )

    data class LanguageSupportLevelMessage(
        val languageSupportLevel: LanguageSupportLevel
    )
}
