package com.dangerfield.spyfall.splash.splash

import spyfallx.coregameapi.Session

interface GetGameInProgress {

    suspend operator fun invoke(): Session?
}
