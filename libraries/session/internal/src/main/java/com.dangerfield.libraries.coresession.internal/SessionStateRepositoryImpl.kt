package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.SessionState
import com.dangerfield.libraries.session.SessionStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SessionStateRepositoryImpl @Inject constructor(): SessionStateRepository {
    override fun getSessionStateFlow(): StateFlow<SessionState> = MutableStateFlow(SessionState.NotInGame)

    override fun getSessionState(): SessionState {
        return SessionState.NotInGame
    }

    override fun updateSessionState(sessionState: SessionState) {

    }

    override fun clearSessionState() {

    }
}