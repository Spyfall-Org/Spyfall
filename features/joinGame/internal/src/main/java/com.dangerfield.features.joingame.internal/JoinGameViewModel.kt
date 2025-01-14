package com.dangerfield.features.joingame.internal

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.dangerfield.features.joingame.internal.JoinGameUseCase.JoinGameError
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.dictionary.getString
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.oddoneoout.features.joingame.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import oddoneout.core.allOrNone
import oddoneout.core.eitherWay
import oddoneout.core.logOnFailure
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class JoinGameViewModel @Inject constructor(
    private val joinGame: JoinGameUseCase,
    private val gameConfig: GameConfig,
    private val dictionary: Dictionary,
    private val getAppLanguageCode: GetAppLanguageCode,
    savedStateHandle: SavedStateHandle
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    override fun initialState() = State(
        accessCodeState = FieldState.Idle(""),
        userNameState = FieldState.Idle(""),
        isLoading = false,
        isFormValid = false
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            is Action.JoinGame -> action.handleJoinGame()
            is Action.RemoveSomethingWentWrong -> action.updateState { it.copy(unresolvableError = null) }
            is Action.UpdateAccessCode -> action.handleUpdateAccessCode()
            is Action.UpdateUserName -> action.handleUpdateUserName()
        }
    }

    fun updateUserName(userName: String) = takeAction(Action.UpdateUserName(userName))

    fun updateAccessCode(accessCode: String) = takeAction(Action.UpdateAccessCode(accessCode))

    fun onSomethingWentWrongDismissed() = takeAction(Action.RemoveSomethingWentWrong)

    fun joinGame() = takeAction(Action.JoinGame)

    private suspend fun Action.JoinGame.handleJoinGame() {
        updateState {
            it.copy(isLoading = true)
        }

        val accessCodeValue = state.accessCodeState.value
        val userNameValue = state.userNameState.value

        allOrNone(
            accessCodeValue,
            userNameValue
        ) { accessCode, userName ->
            joinGame(
                accessCode = accessCode,
                userName = userName
            )
                .onSuccess { game ->
                    sendEvent(
                        Event.GameJoined(accessCode = accessCode,
                            meUserHasDifferentLanguage = getAppLanguageCode() != game.languageCode
                        )
                    )
                }
                .onFailure { throwable ->
                    if (throwable is JoinGameError) {
                        handleJoinGameError(throwable)
                    } else {
                        updateState { it.copy(unresolvableError = UnresolvableError.UnknownError) }
                    }
                }
                .logOnFailure()
                .eitherWay {
                    updateState { it.copy(isLoading = false) }
                }
        } ?: updateState {
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

    private suspend fun Action.JoinGame.handleJoinGameError(joinGameError: JoinGameError) =
        updateState {
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
                            "length" to "${joinGameError.requiredLength}"
                        )
                    )
                )

                is JoinGameError.InvalidNameLength -> it.copy(
                    userNameState = FieldState.Invalid(
                        input = it.userNameState.value,
                        errorMessage = dictionary.getString(
                            R.string.joinGame_userNameFieldInvalidError_text,
                            "min" to "${joinGameError.min}",
                            "max" to "${joinGameError.max}"
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
                    it.copy(unresolvableError = UnresolvableError.IncompatibleGameVersionError(joinGameError.isCurrentLower))
                }

                is JoinGameError.CouldNotFetchPacksNeeded -> {
                    it.copy(unresolvableError = UnresolvableError.CouldNotFetchPacksNeededError)
                }
            }
        }

    override suspend fun mapEachState(state: State): State {
        val isFormValid = state.accessCodeState is FieldState.Valid
                && state.userNameState is FieldState.Valid

        Log.d("Elijah", "STATE UPDATED: \naccess code state is ${state.accessCodeState}\nusername state is ${state.userNameState}\nisFormValid is $isFormValid")
        return state.copy(isFormValid = isFormValid)
    }
    
    private suspend fun Action.UpdateAccessCode.handleUpdateAccessCode() {
        updateState {
            it.copy(accessCodeState = FieldState.Valid(accessCode))
        }

        updateStateDebounced(1.seconds) {
            val state = when {
                accessCode.isEmpty() -> FieldState.Idle(accessCode)
                accessCode.length != gameConfig.accessCodeLength -> {
                    FieldState.Invalid(
                        input = accessCode,
                        errorMessage = dictionary.getString(
                            R.string.joinGame_accessCodeLengthError_text,
                            "length" to "${gameConfig.accessCodeLength}"
                        )
                    )
                }

                else -> FieldState.Valid(accessCode)
            }
            it.copy(accessCodeState = state)
        }
    }

    private suspend fun Action.UpdateUserName.handleUpdateUserName()
    {
        updateState {
            it.copy(userNameState = FieldState.Valid(userName))
        }

        updateStateDebounced(1.seconds) {
            val state = when {
                userName.isEmpty() -> FieldState.Idle(userName)
                userName.length !in gameConfig.minNameLength..gameConfig.maxNameLength -> {
                    FieldState.Invalid(
                        input = userName,
                        errorMessage = dictionary.getString(
                            R.string.joinGame_nameLengthError_text,
                            "min" to "${gameConfig.minNameLength}",
                            "max" to "${gameConfig.maxNameLength}"
                        )
                    )
                }

                else -> FieldState.Valid(userName)
            }

            it.copy(userNameState = state)
        }
    }
}