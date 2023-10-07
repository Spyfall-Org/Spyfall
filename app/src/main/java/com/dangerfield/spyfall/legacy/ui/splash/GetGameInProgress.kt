package com.dangerfield.spyfall.legacy.ui.splash

import spyfallx.coregameapi.Session

interface GetGameInProgress {

    suspend operator fun invoke(): Session?
}
