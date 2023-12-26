package com.dangerfield.spyfall.startup

import androidx.lifecycle.viewModelScope
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.session.EnsureSessionLoaded
import com.dangerfield.libraries.session.Session
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
import spyfallx.core.Try
import spyfallx.core.failFast
import spyfallx.core.logOnError
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val ensureAppConfigLoaded: EnsureAppConfigLoaded,
    private val ensureSessionLoaded: EnsureSessionLoaded,
    private val isAppUpdateRequired: IsAppUpdateRequired,
    private val sessionFlow: SessionFlow
) : SEAViewModel<State, Unit, Action>() {

    override val initialState = State.Loading

    init {
        takeAction(Action.LoadApp)
    }

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadApp -> loadApp()
        }
    }

    private suspend fun loadApp() {
        tryWithTimeout(10.seconds) {
            startUpTasks.awaitAll().failFast()
        }
            .logOnError()
            .onFailure {
                updateState { State.Error }
            }
            .onSuccess {
                val session = sessionFlow.first()
                val colorPrimitive = getSessionColorPrimitive(session.user.themeConfig)

                updateState {
                    State.Loaded(
                        isUpdateRequired = isAppUpdateRequired().first(),
                        accentColor = colorPrimitive,
                        darkModeConfig = session.user.themeConfig.darkModeConfig
                    )
                }

                viewModelScope.launch {
                    listenForAppUpdateRequired()
                }

                viewModelScope.launch {
                    listenForConfigUpdates()
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
                    if (state is State.Loaded) {
                        state.copy(
                            accentColor = colorPrimitive,
                            darkModeConfig = config.darkModeConfig
                        )
                    } else {
                        state
                    }
                }
            }
    }

    private suspend fun getSessionColorPrimitive(themeConfig: ThemeConfig): ColorPrimitive {
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
                val state = state.value
                if (state is State.Loaded) {
                    updateState {
                        state.copy(isUpdateRequired = isUpdateRequired)
                    }
                }
            }
        }
    }

    private val CoroutineScope.startUpTasks: List<Deferred<Try<Unit>>>
        get() = listOf(
            async { ensureSessionLoaded() },
            async { ensureAppConfigLoaded() }
        )

    sealed class Action {
        data object LoadApp : Action()
    }

    sealed class State {
        data class Loaded(
            val isUpdateRequired: Boolean,
            val accentColor: ColorPrimitive,
            val darkModeConfig: DarkModeConfig
        ) : State()

        data object Loading : State()

        data object Error : State()
    }
}
