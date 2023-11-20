package com.dangerfield.libraries.coresession

sealed class SessionState {
    data object NotInGame : SessionState()
    class InGame(val session: Session) : SessionState()
}

data class Session(
    val username: String,
    val accessCode: String
)

