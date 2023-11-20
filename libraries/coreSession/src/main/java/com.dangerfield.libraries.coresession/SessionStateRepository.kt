package com.dangerfield.libraries.coresession

import kotlinx.coroutines.flow.StateFlow

interface SessionStateRepository {

    fun getSessionStateFlow(): StateFlow<SessionState>

    fun getSessionState(): SessionState

    fun updateSessionState(sessionState: SessionState)

    fun clearSessionState()
}