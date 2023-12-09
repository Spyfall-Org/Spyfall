package com.dangerfield.libraries.session

import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    val session: Session
    val sessionFlow: Flow<Session>
    suspend fun updateActiveGame(activeGame: ActiveGame?)
}