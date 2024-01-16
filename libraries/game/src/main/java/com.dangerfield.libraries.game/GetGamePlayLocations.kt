package com.dangerfield.libraries.game

import oddoneout.core.Try

interface GetGamePlayLocations {
    operator fun invoke(packs: List<Pack>, isSingleDevice: Boolean = false): Try<List<Location>>
}