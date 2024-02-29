package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
import oddoneout.core.Try
import javax.inject.Inject

class PackParser @Inject constructor() {

    fun parsePacks(name: String, data: Map<String, Any>) : Try<List<LocationPack>> = Try {
        val packs = data.get("packs") as List<Map<String, Any>>
        packs.map { pack ->
            val packName = pack["name"] as String
            val locations = pack["locations"] as List<Map<String, Any>>

            LocationPack(
                name = packName,
                locations = locations.map { location ->
                    Location(
                        name = location["name"] as String,
                        roles = location["roles"] as List<String>,
                        packName = packName
                    )
                }
            )
        }
    }
}