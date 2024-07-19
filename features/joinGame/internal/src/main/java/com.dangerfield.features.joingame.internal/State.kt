package com.dangerfield.features.joingame.internal

import com.dangerfield.libraries.ui.FieldState

sealed class Action {
    data class UpdateUserName(val userName: String) : Action()
    data class UpdateAccessCode(val accessCode: String) : Action()
    data object JoinGame : Action()
    data object RemoveSomethingWentWrong : Action()
}

data class State(
    val accessCodeState: FieldState<String>,
    val userNameState: FieldState<String>,
    val isLoading: Boolean,
    val isFormValid: Boolean,
    val unresolvableError: UnresolvableError? = null
)

sealed class UnresolvableError {
    data class IncompatibleGameVersionError(val isCurrentLower: Boolean): UnresolvableError()
    data object UnknownError: UnresolvableError()
    data object CouldNotFetchPacksNeededError: UnresolvableError()
}

sealed class Event {
    data class GameJoined(
        val accessCode: String,
        val meUserHasDifferentLanguage: Boolean
    ) : Event()
}