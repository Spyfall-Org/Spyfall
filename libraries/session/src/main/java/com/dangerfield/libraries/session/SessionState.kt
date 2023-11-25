package com.dangerfield.libraries.session

sealed class SessionState(val session: Session?) {
    data object NotInGame : SessionState(null)
    class InGame(username: String, accessCode: String) : SessionState(Session(username, accessCode))
}

data class Session(
    val username: String,
    val accessCode: String,
)

