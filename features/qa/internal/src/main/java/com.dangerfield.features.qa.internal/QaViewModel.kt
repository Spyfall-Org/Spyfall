package com.dangerfield.features.qa.internal

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.consent.ForceEEAConsentLocation
import com.dangerfield.features.consent.ResetGDRPConsent
import com.dangerfield.libraries.config.ConfigOverride
import com.dangerfield.libraries.config.ConfigOverrideRepository
import com.dangerfield.libraries.config.ConfiguredValue
import com.dangerfield.libraries.config.Experiment
import com.dangerfield.libraries.coreflowroutines.onCollection
import com.dangerfield.libraries.session.SessionFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
TODO cleanup
would be better to just create a QA option and have those injected into the view model
then group them by section or something. idk. I can fix the display here to be more scalable
 */
@HiltViewModel
class QaViewModel @Inject constructor(
    private val configOverrideRepository: ConfigOverrideRepository,
    private val configuredValues: Set<@JvmSuppressWildcards ConfiguredValue<*>>,
    private val experiments: Set<@JvmSuppressWildcards Experiment<*>>,
    private val forceEEAConsentLocation: ForceEEAConsentLocation,
    private val sessionFlow: SessionFlow,
    private val resetGDRPConsent: ResetGDRPConsent
) : ViewModel() {

    private val actions = Channel<Action>(Channel.UNLIMITED)

    val state = flow {
        for (action in actions) handleAction(action)
    }
        .onCollection {
            sessionFlow.collectLatest {
                updateSessionId(it.sessionId?.toString())
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            State(
                consentExperiments = listOf(forceEEAConsentLocation.toDisplayableExperiment()),
                configValues = getConfigValues(),
                experiments = getExperiments(),
            )
        )

    fun addOverride(path: String, value: Any) = actions.trySend(Action.AddOverride(path, value))

    fun resetConsent(activity: Activity) {
        resetGDRPConsent(activity)
    }

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
                },
                consentExperiments = it.consentExperiments.map { consentExperiment ->
                    if (consentExperiment.path == action.path) {
                        consentExperiment.copy(value = action.value)
                    } else {
                        consentExperiment
                    }
                }
            )
        }
    }

    private fun getExperiments() = experiments
        .filter { it.showInQaExperiments }
        .map {
            it.toDisplayableExperiment()
        }

    private fun Experiment<*>.toDisplayableExperiment() = DisplayableExperiment(
        name = displayName,
        description = description,
        path = path,
        value = resolveValue(),
        isDebugOnly = isDebugOnly
    )

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

    /*
    TODO so ill add a usecase called get grouped experiements
    and that will inject all experiment in and group them based on the path. Thats actually a great idea.
     */
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
        val consentExperiments: List<DisplayableExperiment> = emptyList(),
        val sessionId: String? = null
    )

    private suspend inline fun FlowCollector<State>.updateState(function: (State) -> State) {
        val prevValue = state.value
        val nextValue = function(prevValue)
        emit(nextValue)
    }
}