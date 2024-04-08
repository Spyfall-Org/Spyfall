package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
import oddoneout.core.Catching
import javax.inject.Inject

class PackParser @Inject constructor() {

    fun parsePacks(data: Map<String, Any>) : Catching<List<LocationPack>> = Catching {
        val packArray = data["packs"] as List<Map<String, Any>>
        packArray.map {
            val packName = it["name"] as String
            val locations = it["locations"] as List<Map<String,Any>>

            LocationPack(
                name = packName,
                locations = locations.map { location ->
                    Location(
                        name = location["name"] as String,
                        roles = location["roles"] as List<String>,
                        packName = packName,
                    )
                }
            )
        }
    }
}