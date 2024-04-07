package com.dangerfield.libraries.coreflowroutines

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug
import java.util.concurrent.ConcurrentHashMap

/**
 * A view model that revolves around State (S), Events (E) and Actions (A)
 * Encourages a unidirectional flow of data from actions to state.
 *
 * S - State - The state of the view. Should be an immutable class that represents the
 * current state of the view. Should represent view state, NOT view element state
 * See https://developer.android.com/topic/architecture/ui-layer/stateholders#elements-ui
 *
 * E - Event - Events are used to trigger one time events that should not be stored in the state.
 * Examples: Navigation, Showing a toast, etc...
 * Storing one time events in the state requires acknowledgment of the state from the view and can
 * lead to complications and bugs if not careful. Turns out to be easier to just roll with events in
 * a channel.
 *

 * A - Action - Actions are the only way to update state. They represent work to be done either user
 * triggered or from the view model itself.
 * Tips:
 * - If you need data to load on init, have the view model take an action in the init block.
 *
 * This viewmodel backs the state into the saved state handle if the state is savable.
 * See [SavedStateHandle.ACCEPTABLE_CLASSES]
 */
abstract class SEAViewModel<S : Any, E : Any, A : Any>(
    private val savedStateHandle: SavedStateHandle,
    private val initialStateArg: S? = null,
) : ViewModel() {

    private val actions = Channel<A>(Channel.UNLIMITED)
    private val events = Channel<E>(Channel.UNLIMITED)
    private val actionDebouncer = ConcurrentHashMap<String, Channel<suspend (S) -> S>>()
    private val _initialState: S by lazy { initialState() }

    private val mutableStateFlow: MutableStateFlow<S> by lazy {
        MutableStateFlow(
            Catching {
                savedStateHandle.get<S>(StateKey)
            }.getOrNull() ?: _initialState
        )
    }

    /**
     * The flow exposing the state of the view model
     */
    val stateFlow: StateFlow<S>
        get() = mutableStateFlow.mapNotNull {
            Catching { mapEachState(it) }.logOnFailure().throwIfDebug().getOrNull()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _initialState,
        )

    /**
     * The flow exposing events from the view mode
     */
    val eventFlow = events.receiveAsFlow()

    /**
     * The current state value
     */
    val state: S get() = mutableStateFlow.value

    init {
        viewModelScope.launch {
            for (action in actions) {
                handleAction(action = action)
            }
        }
    }

    /**
     * Submits an action to be handled by the view model.
     */
    fun takeAction(action: A) {
        actions.trySend(action)
    }

    /**
     * Updates the state of the view model.
     * This is the only way to update the state.
     *
     * Callers must have an action to update the state, this helps maintain UDF.
     */
    suspend fun A.updateState(f: suspend (S) -> S) {
        Catching {
            mutableStateFlow.update {
                f(it)
            }
        }.logOnFailure("Could not up state for: ${state::class.java.name}").throwIfDebug()
    }

    /**
     * Updates state in a debounced manner.
     *
     * The update will not happen until the debounce time has without another call to
     * `debounceUpdateState` from the same action. Every update from the same action
     * will reset the debounce timer.
     */
    suspend fun A.updateStateDebounced(debounceTime: Long = 500L, f: suspend (S) -> S) {
        val actionIdentifier = this::class.java.simpleName
        val debouncedChannel = actionDebouncer[actionIdentifier]

        if (debouncedChannel == null) {
            val channel = Channel<suspend (S) -> S>(Channel.UNLIMITED)

            actionDebouncer[actionIdentifier] = channel

            channel.receiveAsFlow()
                .debounce(debounceTime)
                .collectIn(viewModelScope) {
                    updateState(it)
                }
        } else {
            debouncedChannel.trySend(f)
        }
    }

    /**
     * Sets the initial state to be emitted by the `states` flow.
     *
     * We use a function to force for lazy initialization of the state, allowing the view model to
     * define the initial state in a more flexible way. Including using SavedStateHandle Args.
     */
    protected open fun initialState(): S {
        return initialStateArg
            ?: throw IllegalStateException("Initial state must be passed in or overridden in the initialState function.")
    }

    /**
     * Events in this code base are synonymous with side effects. They are used to trigger
     * one time events that should not be stored in the state.
     *
     * Examples:
     * - Navigation
     * - Showing a toast
     * - etc...
     */
    fun sendEvent(event: E) {
        events.trySend(event)
    }

    /**
     * Adds a mapper to the state flow. This is useful for mapping the entire state to a different
     * state on each update.
     *
     * The mapping is called safely, if an error is thrown it will be silently caught, logged and
     * will not update the state.
     *
     * Child view models can also always expose their own state stream that maps over the parents
     * if that's preferred.
     *
     * ex:
     * ```
     * val stateStream: StateFlow<State> = this.states.map {
     *    it.copy(something = somethingElse)
     *    }.stateIn(viewModelScope, ...)
     * ```
     */
    protected open suspend fun mapEachState(
        state: S
    ): S {
        return state
    }

    /**
     * Function that ensures all children handle their actions
     *
     * This helps to ensure that the flow is unidirectional.
     *
     * View -> Action -> State -> View
     *
     * @param action the action to be handled
     */
    protected abstract suspend fun handleAction(action: A)

    override fun onCleared() {
        Catching {
            savedStateHandle[StateKey] = state
        }.logOnFailure("Could not save state on clear for state: ${state::class.java.name}")
    }

    companion object {
        private const val StateKey = "state"
    }
}
