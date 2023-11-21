package com.dangerfield.features.joingame.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.libraries.session.SessionStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor(
   // private val sessionStateRepository: SessionStateRepository
): ViewModel() {

    private val actions = Channel<Action>(Channel.UNLIMITED)

    val state = flow<State> {
        for (action in actions) handleAction(action)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        State(
            accessCode = "",
            userName = "",
            joiningState = JoiningState.CollectingInput
        )
    )

    fun updateUserName(userName: String) = actions.trySend(Action.UpdateUserName(userName))

    fun updateAccessCode(accessCode: String) = actions.trySend(Action.UpdateAccessCode(accessCode))

    fun joinGame() = actions.trySend(Action.JoinGame)

    fun resolveNotJoined() = actions.trySend(Action.ResolveJoinGameError)

    private suspend fun FlowCollector<State>.handleAction(action: Action) {
        when(action) {
            Action.JoinGame -> handleJoinGame()
            Action.ResolveJoinGameError -> handleResolveJoinGameError()
            is Action.UpdateAccessCode -> handleUpdateAccessCode(action.accessCode)
            is Action.UpdateUserName -> handleUpdateUserName(action.userName)
        }
    }

    private suspend fun FlowCollector<State>.handleJoinGame() {
        updateState { it.copy(joiningState = JoiningState.JoiningGame) }
        // validate code
        // use game repo to get game with access code
        // use Validation Use cases to validate name
        // on success update the session state and update state to joined
        // on failure update state to not joined
    }

    private suspend fun FlowCollector<State>.handleResolveJoinGameError() = updateState {
        it.copy(joiningState = JoiningState.CollectingInput)
    }

    private suspend fun FlowCollector<State>.handleUpdateAccessCode(accessCode: String) = updateState {
        it.copy(accessCode = accessCode)
    }

    private suspend fun FlowCollector<State>.handleUpdateUserName(userName: String) = updateState {
        it.copy(userName = userName)
    }

    private suspend inline fun FlowCollector<State>.updateState(function: (State) -> State) {
        val prevValue = state.value
        val nextValue = function(prevValue)
        emit(nextValue)
    }

    sealed class Action {
        data class UpdateUserName(val userName: String): Action()
        data class UpdateAccessCode(val accessCode: String): Action()
        data object JoinGame: Action()
        data object ResolveJoinGameError: Action()
    }

    data class State(
        val accessCode: String,
        val userName: String,
        val joiningState: JoiningState,
    )

    sealed class JoiningState {
        data object CollectingInput: JoiningState()
        data object JoiningGame: JoiningState()
        data object JoinedGame: JoiningState()

        sealed class CouldNotJoin: JoiningState() {
            data object InvalidName: CouldNotJoin()
            data object InvalidAccessCode: CouldNotJoin()
            data object AccessCodeNotFound: CouldNotJoin()
            data object GameAlreadyStarted: CouldNotJoin()
            data object GameHasMaxPlayers: CouldNotJoin()
            data object UsernameTaken: CouldNotJoin()
            data object JoinTimedOut: CouldNotJoin()
        }
    }
}