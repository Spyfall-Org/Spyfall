package com.dangerfield.features.joingame.internal

import com.dangerfield.features.joingame.internal.JoinGameUseCase.JoinGameError
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.oddoneoout.features.joingame.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import oddoneout.core.allOrNone
import oddoneout.core.logOnError
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor(
    private val joinGame: JoinGameUseCase,
    private val gameConfig: GameConfig,
    private val dictionary: Dictionary,
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
                    errorMessage = dictionary.getString(R.string.joinGame_accessCodeFieldEmpty_text)
                ),
                userNameState = FieldState.Invalid(
                    input = it.userNameState.value,
                    errorMessage = dictionary.getString(R.string.joinGame_userNameFieldEmptError_text)
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
                        errorMessage = dictionary.getString(R.string.joinGame_accessCodeNotFoundError_text)
                    )
                )

                is JoinGameError.GameAlreadyStarted -> it.copy(
                    accessCodeState = FieldState.Invalid(
                        input = it.accessCodeState.value,
                        errorMessage = dictionary.getString(R.string.joinGame_gameAlreadyStartedError_text)
                    )
                )

                is JoinGameError.GameHasMaxPlayers -> it.copy(
                    accessCodeState = FieldState.Invalid(
                        input = it.accessCodeState.value,
                        errorMessage = dictionary.getString(R.string.joinGame_gameHasMaxPlayersError_text)
                    )
                )

                is JoinGameError.InvalidAccessCodeLength -> it.copy(
                    accessCodeState = FieldState.Invalid(
                        input = it.accessCodeState.value,
                        errorMessage = dictionary.getString(
                            R.string.joinGame_invalidAccessCodeError_text,
                            mapOf("length" to "${joinGameError.requiredLength}")
                        )
                    )
                )

                is JoinGameError.InvalidNameLength -> it.copy(
                    userNameState = FieldState.Invalid(
                        input = it.userNameState.value,
                        errorMessage = dictionary.getString(
                            R.string.joinGame_userNameFieldInvalidError_text,
                            mapOf(
                                "min" to "${joinGameError.min}",
                                "max" to "${joinGameError.max}"
                            )
                        )
                    )
                )

                is JoinGameError.UsernameTaken -> it.copy(
                    userNameState = FieldState.Invalid(
                        input = it.userNameState.value,
                        errorMessage = dictionary.getString(R.string.joinGame_userNameTakenError_text)
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
                        errorMessage = dictionary.getString(
                            R.string.joinGame_accessCodeLengthError_text,
                            mapOf("length" to "${gameConfig.accessCodeLength}")
                        )
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
                    errorMessage = dictionary.getString(
                        R.string.joinGame_nameLengthError_text,
                        mapOf(
                            "min" to "${gameConfig.minNameLength}",
                            "max" to "${gameConfig.maxNameLength}"

                        )
                    )
                )
            }

            else -> FieldState.Valid(userName)
        }

        it.copy(userNameState = state)
    }
}