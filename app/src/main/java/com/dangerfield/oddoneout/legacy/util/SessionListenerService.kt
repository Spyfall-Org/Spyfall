package com.dangerfield.oddoneout.legacy.util

import com.dangerfield.oddoneout.legacy.models.Session

interface SessionListenerService {
    fun addListener(
        sessionUpdater: SessionUpdater,
        session: Session
    )

    fun removeListener()
    fun isListening() : Boolean
}
