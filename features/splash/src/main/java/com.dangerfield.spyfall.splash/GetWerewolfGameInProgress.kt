package com.dangerfield.spyfall.splash

import spyfallx.coregameapi.Session
import javax.inject.Inject

class GetWerewolfGameInProgress @Inject constructor() : GetGameInProgress {
    override suspend fun invoke(): Session? {
        return null
    }
}
