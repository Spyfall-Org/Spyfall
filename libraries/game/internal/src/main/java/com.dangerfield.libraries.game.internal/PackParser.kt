package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.Pack
import oddoneout.core.Try
import javax.inject.Inject

class PackParser @Inject constructor() {

    fun parsePack(name: String, data: Map<String, Any>) : Try<Pack> = Try {
        val locations = data.entries.map { (name, locations) ->
            Location(
                name = name,
                roles = locations as List<String>,
                packName = name
            )
        }

        Pack(
            name = name,
            locations = locations
        )
    }
}