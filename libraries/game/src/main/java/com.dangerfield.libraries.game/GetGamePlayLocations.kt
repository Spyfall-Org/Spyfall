package com.dangerfield.libraries.game

import spyfallx.core.Try

interface GetGamePlayLocations {
    operator fun invoke(packs: List<Pack>): Try<List<Location>>
}