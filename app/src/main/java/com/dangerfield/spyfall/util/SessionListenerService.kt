package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.models.Session

interface SessionListenerService {
    fun addListener(
        sessionUpdater: SessionUpdater,
        session: Session
    )

    fun removeListener()
    fun isListening() : Boolean
}