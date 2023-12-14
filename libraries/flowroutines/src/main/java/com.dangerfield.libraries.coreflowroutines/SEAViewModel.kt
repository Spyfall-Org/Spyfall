package com.dangerfield.libraries.coreflowroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug

/**
 * A view model that revolves around State (S), Events (E) and Actions (A)
 */
abstract class SEAViewModel<S, E, A> : ViewModel() {

    protected abstract val initialState: S

    private val actions = Channel<A>(Channel.UNLIMITED)
    private val _events = Channel<E>(Channel.UNLIMITED)
    private val _state: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }
    val events = _events.receiveAsFlow()
    val state: StateFlow<S> get() = _state

    init {
        viewModelScope.launch {
            for (action in actions) handleAction(action)
        }
    }

    fun takeAction(action: A) {
        actions.trySend(action)
    }

    fun sendEvent(event: E) {
        _events.trySend(event)
    }

    protected suspend fun updateState(update: suspend (S) -> S) {
        Try {
            _state.update { update(it) }
        }
            .logOnError("Could not update state")
            .throwIfDebug()
    }

    protected abstract suspend fun handleAction(action: A)
}