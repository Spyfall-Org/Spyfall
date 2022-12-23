package com.dangerfield.spyfall.splash.spyfall

import com.dangerfield.spyfall.splash.splash.GetGameInProgress
import spyfallx.coregame.SpyfallGamePreferences
import spyfallx.coregame.SpyfallRepository
import spyfallx.coregame.SpyfallSession
import javax.inject.Inject

class GetSpyfallGameInProgress @Inject constructor(
    private val spyfallGamePreferences: SpyfallGamePreferences,
    private val spyfallRepository: SpyfallRepository
) : GetGameInProgress {
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