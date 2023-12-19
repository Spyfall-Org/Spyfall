package com.dangerfield.features.qa.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.libraries.config.ConfigOverride
import com.dangerfield.libraries.config.ConfigOverrideRepository
import com.dangerfield.libraries.config.ConfiguredValue
import com.dangerfield.libraries.config.Experiment
import com.dangerfield.libraries.coreflowroutines.launchOnStart
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.SessionFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QaViewModel @Inject constructor(
    private val configOverrideRepository: ConfigOverrideRepository,
    private val configuredValues: Set<@JvmSuppressWildcards ConfiguredValue<*>>,
    private val experiments: Set<@JvmSuppressWildcards Experiment<*>>,
    private val sessionFlow: SessionFlow
) : ViewModel() {

    private val actions = Channel<Action>(Channel.UNLIMITED)

    val state = flow {
        for (action in actions) handleAction(action)
    }
        .launchOnStart {
            sessionFlow.collectLatest {
                updateSessionId(it.sessionId?.toString())
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            State(
                configValues = getConfigValues(),
                experiments = getExperiments(),
            )
        )

    fun addOverride(path: String, value: Any) = actions.trySend(Action.AddOverride(path, value))

    private suspend fun FlowCollector<State>.handleAction(action: Action) {
        when (action) {
            is Action.AddOverride -> {
                applyOverrideOptimistically(action)
                configOverrideRepository.addOverride(
                    ConfigOverride(
                        path = action.path,
                        value = action.value
                    )
                )
            }

            is Action.UpdateSessionId -> {
                updateState {
                    it.copy(sessionId = action.sessionId)
                }
            }
        }
    }

    private suspend fun FlowCollector<State>.applyOverrideOptimistically(
        action: Action.AddOverride
    ) {
        updateState {
            it.copy(
                configValues = it.configValues.map { configValue ->
                    if (configValue.path == action.path) {
                        configValue.copy(value = action.value)
                    } else {
                        configValue
                    }
                },
                experiments = it.experiments.map { experiment ->
                    if (experiment.path == action.path) {

                        experiment.copy(value = action.value)
                    } else {
                        experiment
                    }
                }
            )
        }
    }

    private fun getExperiments() = experiments
        .filter { it.showInQADashboard }
        .map {
            DisplayableExperiment(
                name = it.displayName,
                description = it.description,
                path = it.path,
                value = it.resolveValue(),
                isDebugOnly = it.isDebugOnly
            )
        }

    private fun getConfigValues() = configuredValues
        .filter { it.showInQADashboard }
        .map {
            DisplayableConfigValue(
                name = it.displayName,
                path = it.path,
                value = it.resolveValue(),
                description = it.description
            )
        }

    private fun updateSessionId(sessionId: String?) {
        viewModelScope.launch {
            actions.send(Action.UpdateSessionId(sessionId))
        }
    }

    data class DisplayableConfigValue(
        val name: String,
        val description: String?,
        val path: String,
        val value: Any,
    )

    data class DisplayableExperiment(
        val name: String,
        val description: String?,
        val path: String,
        val value: Any,
        val isDebugOnly: Boolean
    )

    sealed class Action {
        class AddOverride(val path: String, val value: Any) : Action()
        internal class UpdateSessionId(val sessionId: String?) : Action()
    }

    data class State(
        val configValues: List<DisplayableConfigValue>,
        val experiments: List<DisplayableExperiment>,
        val sessionId: String? = null
    )

    private suspend inline fun FlowCollector<State>.updateState(function: (State) -> State) {
        val prevValue = state.value
        val nextValue = function(prevValue)
        emit(nextValue)
    }
}