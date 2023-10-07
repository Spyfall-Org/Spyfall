package com.dangerfield.spyfall.legacy.api

import spyfallx.coregame.SpyfallGamePreferences
import spyfallx.coregame.SpyfallRepository
import spyfallx.coregame.SpyfallSession
import javax.inject.Inject

class GetSpyfallGameInProgress @Inject constructor(
    private val spyfallGamePreferences: SpyfallGamePreferences,
    private val spyfallRepository: SpyfallRepository
) : com.dangerfield.spyfall.legacy.ui.splash.GetGameInProgress {
    override suspend fun invoke(): SpyfallSession? {
        return spyfallGamePreferences.session?.let { sessionFound ->
            if (!spyfallRepository.gameExists(sessionFound.accessCode)) {
                spyfallGamePreferences.session = null
                null
            } else {
                sessionFound
            }
        }
    }
}
