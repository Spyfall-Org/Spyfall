package com.dangerfield.features.newgame.internal.presentation.model

import com.dangerfield.libraries.ui.FieldState

sealed class CreateGameError: Throwable() {
    class NameBlank : CreateGameError()
    class PacksEmpty : CreateGameError()
    class TimeLimitInvalid : CreateGameError()
    class TimeLimitTooLong : CreateGameError()
    class TimeLimitTooShort : CreateGameError()
    class InvalidNumberOfPlayers : CreateGameError()
    class TooManyPlayers : CreateGameError()
    class TooFewPlayers : CreateGameError()
    class VideoCallLinkInvalid: CreateGameError()
}

sealed class Action {
    class UpdateName(val name: String) : Action()
    class UpdateVideoCallLink(val link: String) : Action()
    data object ResolveErrors : Action()
    class UpdateTimeLimit(val timeLimit: String) : Action()
    class UpdateGameType(val isSingleDevice: Boolean) : Action()
    class UpdateNumOfPlayers(val numOfPlayers: String) : Action()
    class SelectPack(val pack: PackOption.Pack, val isSelected: Boolean) : Action()
    data object CreateGame : Action()
    data object Init : Action()
    data object LoadPacks: Action()
}

sealed class Event {
    data class GameCreated(
        val accessCode: String,
        val videoCallLink: String?
    ) : Event()

    data class SingleDeviceGameCreated(val accessCode: String) : Event()
}

data class State(
    val packsState: FieldState<List<PackOption>>,
    val timeLimitState: FieldState<String>,
    val nameState: FieldState<String>,
    val videoCallLinkState: FieldState<String>,
    val isLoadingPacks: Boolean,
    val isLoadingCreation: Boolean,
    val isSingleDevice: Boolean,
    val numberOfPlayersState: FieldState<String>,
    val didCreationFail: Boolean = false,
    val didLoadFail: Boolean = false,
    val formState: FormState,
    val isOffline: Boolean,
)

fun State.selectedPacks() = packsState.value
    ?.filter { it is PackOption.Pack && it.isSelected }
    ?.mapNotNull { it.packData }

fun State.timeLimit() = timeLimitState.value?.toIntOrNull()

fun State.numberOfPlayers() = numberOfPlayersState.value?.toIntOrNull()

fun State.userName() = nameState.value

sealed class FormState {
    data object Idle : FormState()
    data object Valid : FormState()
    data object Invalid : FormState()
}
