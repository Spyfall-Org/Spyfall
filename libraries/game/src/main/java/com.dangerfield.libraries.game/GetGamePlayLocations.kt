package com.dangerfield.libraries.game

import oddoneout.core.Catching

interface GetGamePlayLocations {
    operator fun invoke(locationPacks: List<LocationPack>, isSingleDevice: Boolean = false): Catching<List<Location>>
}