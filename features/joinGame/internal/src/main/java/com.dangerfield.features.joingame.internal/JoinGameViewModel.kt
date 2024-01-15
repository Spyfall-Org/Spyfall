package com.dangerfield.features.joingame.internal

import com.dangerfield.features.joingame.internal.JoinGameUseCase.JoinGameError
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.ui.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import spyfallx.core.allOrNone
import spyfallx.core.logOnError
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor(
    private val joinGame: JoinGameUseCase,
    private val gameConfig: GameConfig
) : SEAViewModel<State, Event, Action>() {

    override val initialState = State(
        accessCodeState = FieldState.Idle(""),
        userNameState = FieldState.Idle(""),
        isLoading = false,
        isFormValid = false
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.JoinGame -> handleJoinGame()
            Action.RemoveSomethingWentWrong -> updateStateWithValidation { it.copy(unresolvableError = null) }
            is Action.UpdateAccessCode -> handleUpdateAccessCode(action.accessCode)
            is Action.UpdateUserName -> handleUpdateUserName(action.userName)
        }
    }

    fun updateUserName(userName: String) = takeAction(Action.UpdateUserName(userName))

    fun updateAccessCode(accessCode: String) = takeAction(Action.UpdateAccessCode(accessCode))

    fun onSomethingWentWrongDismissed() = takeAction(Action.RemoveSomethingWentWrong)

    fun joinGame() = takeAction(Action.JoinGame)

    private suspend fun handleJoinGame() {
        updateStateWithValidation { it.copy(isLoading = true) }

        val accessCodeValue = state.value.accessCodeState.value
        val userNameValue = state.value.userNameState.value

        allOrNone(one = accessCodeValue, two = userNameValue) { accessCode, userName ->
            joinGame(
                accessCode = accessCode,
                userName = userName
            )
                .onSuccess {
                    this@JoinGameViewModel.sendEvent(Event.GameJoined(accessCode))
                }
                .onFailure { throwable ->
                    if (throwable is JoinGameError) {
                        handleJoinGameError(throwable)
                    } else {
                        updateStateWithValidation { it.copy(unresolvableError = UnresolvableError.UnknownError) }
                    }
                }
                .logOnError()
                .eitherWay {
                    updateStateWithValidation { it.copy(isLoading = false) }
                }
        } ?: updateStateWithValidation {
            it.copy(
                accessCodeState = FieldState.Invalid(
                    input = it.accessCodeState.value,
                    errorMessage = "Please enter an access code"
                ),
                userNameState = FieldState.Invalid(
                    input = it.userNameState.value,
                    errorMessage = "Please enter a username"
                )
            )
        }
    }

    private suspend fun handleJoinGameError(joinGameError: JoinGameError) =
        updateStateWithValidation {
            when (joinGameError) {
                is JoinGameError.GameNotFound -> it.copy(
                    accessCodeState = FieldState.Invalid(
                        input = it.accessCodeState.value,
                        errorMessage = "Game with that access code was not found"

                    )
                )

                is JoinGameError.GameAlreadyStarted -> it.copy(
                    accessCodeState = FieldState.Invalid(
                        input = it.accessCodeState.value,
                        errorMessage = "Game with that access code was already started"

                    )
                )

                is JoinGameError.GameHasMaxPlayers -> it.copy(
                    accessCodeState = FieldState.Invalid(
                        input = it.accessCodeState.value,
                        errorMessage = "Game with that access code already has the maximum number of players"

                    )
                )

                is JoinGameError.InvalidAccessCodeLength -> it.copy(
                    accessCodeState = FieldState.Invalid(
                        input = it.accessCodeState.value,
                        errorMessage = "That access code seems to be invalid. Access codes are supposed to be ${joinGameError.requiredLength} characters long."

                    )
                )

                is JoinGameError.InvalidNameLength -> it.copy(
                    userNameState = FieldState.Invalid(
                        input = it.userNameState.value,
                        errorMessage = "Names must be between ${joinGameError.min} - ${joinGameError.max} characters long."

                    )
                )

                is JoinGameError.UsernameTaken -> it.copy(
                    userNameState = FieldState.Invalid(
                        input = it.userNameState.value,
                        errorMessage = "That username is taken. Please try to think of something more original."

                    )
                )

                is JoinGameError.UnknownError -> {
                    it.copy(unresolvableError = UnresolvableError.UnknownError)
                }

                is JoinGameError.IncompatibleVersion -> {
                    it.copy(unresolvableError = UnresolvableError.IncompatibleError(joinGameError.isCurrentLower))
                }
            }
        }

    private suspend fun updateStateWithValidation(update: suspend (State) -> State) = updateState {
        val newState = update(it)

        val isFormValid = newState.accessCodeState is FieldState.Valid
                && newState.userNameState is FieldState.Valid

        newState.copy(isFormValid = isFormValid)
    }

    private suspend fun handleUpdateAccessCode(accessCode: String) =
        updateStateWithValidation {
            val state = when {
                accessCode.isEmpty() -> FieldState.Idle(accessCode)
                accessCode.length != gameConfig.accessCodeLength -> {
                    FieldState.Invalid(
                        input = accessCode,
                        errorMessage = "Access codes are ${gameConfig.accessCodeLength} characters long."
                    )
                }

                else -> FieldState.Valid(accessCode)
            }

            it.copy(accessCodeState = state)
        }

    private suspend fun handleUpdateUserName(userName: String) = updateStateWithValidation {
        val state = when {
            userName.isEmpty() -> FieldState.Idle(userName)
            userName.length !in gameConfig.minNameLength..gameConfig.maxNameLength -> {
                FieldState.Invalid(
                    input = userName,
                    errorMessage = "Names must be between ${gameConfig.minNameLength} - ${gameConfig.maxNameLength} characters long."
                )
            }
            else -> FieldState.Valid(userName)
        }

        it.copy(userNameState = state)
    }
}