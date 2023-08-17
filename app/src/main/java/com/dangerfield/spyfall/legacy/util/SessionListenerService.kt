package com.dangerfield.spyfall.legacy.util

import com.dangerfield.spyfall.legacy.models.Session

interface SessionListenerService {
    fun addListener(
        sessionUpdater: SessionUpdater,
        session: Session
    )

    fun removeListener()
    fun isListening() : Boolean
}
