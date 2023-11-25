package com.dangerfield.features.joingame.internal

sealed class Action {
    data class UpdateUserName(val userName: String) : Action()
    data class UpdateAccessCode(val accessCode: String) : Action()
    data object JoinGame : Action()
    data object RemoveSomethingWentWrong : Action()
}

data class State(
    val accessCodeState: AccessCodeState,
    val userNameState: UserNameState,
    val isLoading: Boolean,
    val unresolvableError: UnresolvableError? = null
)

sealed class UnresolvableError {
    data class IncompatibleError(val isCurrentLower: Boolean): UnresolvableError()
    data object UnknownError: UnresolvableError()
}

fun State.withNoErrors(): State =
    copy(
        accessCodeState = accessCodeState.copy(
            gameDoesNotExist = false,
            maxPlayersError = null,
            gameAlreadyStarted = false,
            invalidLengthError = null,
        ),
        userNameState = userNameState.copy(
            invalidNameLengthError = null,
            isTaken = false,
        )
    )

data class AccessCodeState(
    val value: String,
    val gameDoesNotExist: Boolean = false,
    val maxPlayersError: MaxPlayersError? = null,
    val gameAlreadyStarted: Boolean = false,
    val invalidLengthError: InvalidAccessCodeLengthError? = null,
)

data class MaxPlayersError(val max: Int)
data class InvalidAccessCodeLengthError(val requiredLength: Int)
data class InvalidNameLengthError(val min: Int, val max: Int)

data class UserNameState(
    val value: String,
    val invalidNameLengthError: InvalidNameLengthError? = null,
    val isTaken: Boolean = false,
)

sealed class Event {
    data object GameJoined : Event()
}