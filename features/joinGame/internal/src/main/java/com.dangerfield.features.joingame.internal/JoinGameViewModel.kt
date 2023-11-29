package com.dangerfield.features.joingame.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.joingame.internal.JoinGameUseCase.JoinGameError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import spyfallx.core.logOnError
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor(
    private val joinGame: JoinGameUseCase
) : ViewModel() {

    private val actions = Channel<Action>(Channel.UNLIMITED)
    private val _events = Channel<Event>()

    val events = _events.receiveAsFlow()

    val state = flow {
        for (action in actions) handleAction(action)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        State(
            accessCodeState = AccessCodeState(value = ""),
            userNameState = UserNameState(value = ""),
            isLoading = false,
            unresolvableError = null
        )
    )

    fun updateUserName(userName: String) = actions.trySend(Action.UpdateUserName(userName))

    fun updateAccessCode(accessCode: String) = actions.trySend(Action.UpdateAccessCode(accessCode))

    fun onSomethingWentWrongDismissed() = actions.trySend(Action.RemoveSomethingWentWrong)

    fun joinGame() = actions.trySend(Action.JoinGame)

    private suspend fun FlowCollector<State>.handleAction(action: Action) {
        updateState { it.withNoErrors() }
        when (action) {
            is Action.RemoveSomethingWentWrong -> updateState { it.copy(unresolvableError = null) }
            is Action.JoinGame -> handleJoinGame()
            is Action.UpdateAccessCode -> handleUpdateAccessCode(action.accessCode)
            is Action.UpdateUserName -> handleUpdateUserName(action.userName)
        }
    }

    private suspend fun FlowCollector<State>.handleJoinGame() {
        updateState { it.copy(isLoading = true) }

        joinGame(
            accessCode = state.value.accessCodeState.value,
            userName = state.value.userNameState.value
        )
            .onSuccess { _events.trySend(Event.GameJoined) }
            .logOnError()
            .onFailure { throwable ->
                if (throwable is JoinGameError) {
                    handleJoinGameError(throwable)
                } else {
                    updateState { it.copy(unresolvableError = UnresolvableError.UnknownError) }
                }
            }
            .eitherWay {
                updateState { it.copy(isLoading = false) }
            }
    }

    private suspend fun FlowCollector<State>.handleJoinGameError(joinGameError: JoinGameError) =
        updateState {
            when (joinGameError) {
                is JoinGameError.GameNotFound -> it.copy(
                    accessCodeState = it.accessCodeState.copy(gameDoesNotExist = true)
                )

                is JoinGameError.GameAlreadyStarted -> it.copy(
                    accessCodeState = it.accessCodeState.copy(gameAlreadyStarted = true)
                )

                is JoinGameError.GameHasMaxPlayers -> it.copy(
                    accessCodeState = it.accessCodeState.copy(
                        maxPlayersError = MaxPlayersError(
                            max = joinGameError.max
                        )
                    )
                )

                is JoinGameError.InvalidAccessCodeLength -> it.copy(
                    accessCodeState = it.accessCodeState.copy(
                        invalidLengthError = InvalidAccessCodeLengthError(
                            requiredLength = joinGameError.requiredLength,
                        )
                    )
                )

                is JoinGameError.InvalidNameLength -> it.copy(
                    userNameState = it.userNameState.copy(
                        invalidNameLengthError = InvalidNameLengthError(
                            min = joinGameError.min,
                            max = joinGameError.max
                        )
                    )
                )

                is JoinGameError.UsernameTaken -> it.copy(
                    userNameState = it.userNameState.copy(isTaken = true)
                )

                is JoinGameError.UnknownError -> {
                    it.copy(unresolvableError = UnresolvableError.UnknownError)
                }

                is JoinGameError.IncompatibleVersion -> {
                    it.copy(unresolvableError = UnresolvableError.IncompatibleError(joinGameError.isCurrentLower))
                }
            }
        }

    private suspend fun FlowCollector<State>.handleUpdateAccessCode(accessCode: String) =
        updateState {
            it.copy(accessCodeState = it.accessCodeState.copy(value = accessCode))
        }

    private suspend fun FlowCollector<State>.handleUpdateUserName(userName: String) = updateState {
        it.copy(userNameState = it.userNameState.copy(value = userName))
    }

    private suspend inline fun FlowCollector<State>.updateState(function: (State) -> State) {
        val prevValue = state.value
        val nextValue = function(prevValue)
        emit(nextValue)
    }

    // TODO create an option for players that have already started to go back to waiting room
    // and let more players join
}