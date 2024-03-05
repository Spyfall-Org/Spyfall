package com.dangerfield.libraries.game

import oddoneout.core.Try

interface GetGamePlayLocations {
    operator fun invoke(locationPacks: List<LocationPack>, isSingleDevice: Boolean = false): Try<List<Location>>
}