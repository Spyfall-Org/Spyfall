package com.dangerfield.spyfall.startup

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.EnsureSessionLoaded
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.SessionFlow
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.spyfall.startup.MainActivityViewModel.Action
import com.dangerfield.spyfall.startup.MainActivityViewModel.Event
import com.dangerfield.spyfall.startup.MainActivityViewModel.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import spyfallx.core.Try
import spyfallx.core.failFast
import spyfallx.core.logOnError
import com.dangerfield.libraries.ui.color.ThemeColor
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val ensureAppConfigLoaded: EnsureAppConfigLoaded,
    private val ensureSessionLoaded: EnsureSessionLoaded,
    private val isAppUpdateRequired: IsAppUpdateRequired,
    private val sessionFlow: SessionFlow
) : SEAViewModel<State, Event, Action>() {

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
            .onFailure { updateState { State.Error } }
            .onSuccess {
                val colorPrimitive = getSessionColorPrimitive(sessionFlow.first())

                updateState {
                    State.Loaded(
                        isUpdateRequired = isAppUpdateRequired().first(),
                        accentColor = colorPrimitive
                    )
                }

                viewModelScope.launch {
                    listenForAppUpdateRequired()
                }

                viewModelScope.launch {
                    listenForSessionColorUpdates()
                }
            }
    }

    private suspend fun listenForSessionColorUpdates() {
        sessionFlow.collectLatest {
            val colorPrimitive = getSessionColorPrimitive(it)
            Log.d("Elijah", "listenForSessionColorUpdates: $colorPrimitive")
            updateState { state ->
                if (state is State.Loaded) {
                    state.copy(accentColor = colorPrimitive)
                } else {
                    state
                }
            }
        }
    }

    private suspend fun getSessionColorPrimitive(session: Session): ColorPrimitive {
        val initialColorConfig = session.user.themeConfig.colorConfig
        val colorPrimitive = if (initialColorConfig is ColorConfig.Specific) {
            initialColorConfig.color.colorPrimitive
        } else {
            ThemeColor.entries.random().colorPrimitive
        }
        return colorPrimitive
    }

    private fun listenForAppUpdateRequired() {
        viewModelScope.launch {
            isAppUpdateRequired().collectLatest { isUpdateRequired ->
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

    sealed class Event {

    }

    sealed class State {
        data class Loaded(
            val isUpdateRequired: Boolean,
            val accentColor: ColorPrimitive
        ) : State()

        data object Loading : State()

        data object Error : State()
    }
}
