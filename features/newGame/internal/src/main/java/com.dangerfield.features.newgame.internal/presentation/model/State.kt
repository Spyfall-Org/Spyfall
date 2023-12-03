package com.dangerfield.features.newgame.internal.presentation.model

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class CreateGameError {
    data object NameBlank : CreateGameError()
    data object PacksEmpty : CreateGameError()
    data object TimeLimitInvalid : CreateGameError()
    data object TimeLimitTooLong : CreateGameError()
    data object TimeLimitTooShort : CreateGameError()
    data object InvalidNumberOfPlayers : CreateGameError()
    data object TooManyPlayers : CreateGameError()
    data object TooFewPlayers : CreateGameError()
}

sealed class Action {
    class UpdateName(val name: String) : Action()
    class UpdateVideoCallLink(val link: String) : Action()
    class UpdateTimeLimit(val timeLimit: String) : Action()
    class UpdateGameType(val isSingleDevice: Boolean) : Action()
    class UpdateNumOfPlayers(val numOfPlayers: String) : Action()
    class SelectPack(val pack: DisplayablePack, val isSelected: Boolean) : Action()
    data object CreateGame : Action()
    data object LoadPacks : Action()
}

sealed class Event {
    data class GameCreated(
        val accessCode: String,
        val videoCallLink: String?
    ) : Event()

    data object SingleDeviceGameCreated : Event()
}

data class State(
    val packsState: FieldState<List<DisplayablePack>>,
    val timeLimitState: FieldState<String>,
    val nameState: FieldState<String>,
    val videoCallLinkState: FieldState<String>,
    val isLoadingPacks: Boolean,
    val isLoadingCreation: Boolean,
    val isSingleDevice: Boolean,
    val numberOfPlayersState: FieldState<String>,
    val didSomethingGoWrong: Boolean = false,
    val formState: FormState,
)

fun State.selectedPacks() = packsState.backingValue
    ?.filter { it.isSelected }
    ?.map { it.pack }

fun State.timeLimit() = timeLimitState.backingValue?.toIntOrNull()

fun State.numberOfPlayers() = numberOfPlayersState.backingValue?.toIntOrNull()

fun State.userName() = nameState.backingValue

sealed class FormState {
    data object Idle : FormState()
    data object Valid : FormState()
    data object Invalid : FormState()
}

sealed class FieldState<out T>(val backingValue: T?) {
    data class Idle<T>(val value: T) : FieldState<T>(value)
    data class Valid<T>(val value: T) : FieldState<T>(value)
    data class Invalid<T>(val value: T?, val errorMessage: String) : FieldState<T>(value)
    data class Error<T>(val value: T? = null, val errorMessage: String? = null) :
        FieldState<T>(value)

    @OptIn(ExperimentalContracts::class)
    fun isInvalid(): Boolean {
        contract {
            returns(true) implies (this@FieldState is Invalid<*>)
        }
        return this is Invalid
    }

    @OptIn(ExperimentalContracts::class)
    fun isValid(): Boolean {
        contract {
            returns(true) implies (this@FieldState is Valid<*>)
        }
        return this is Valid
    }
}

sealed class GameType {
    class SingleDevice(
        val numberOfPlayersState: FieldState<Int>,
    ) : GameType()

    data object MultiDevice : GameType()
}