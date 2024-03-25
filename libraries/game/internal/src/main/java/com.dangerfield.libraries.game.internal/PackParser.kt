package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
import oddoneout.core.Catching
import javax.inject.Inject

class PackParser @Inject constructor() {

    fun parsePack(name: String, data: Map<String, Any>?) : Catching<LocationPack> = Catching {
        LocationPack(
            name = name,
            locations = data!!.entries.map { (location, roles) ->
                Location(
                    name = location as String,
                    roles = roles as List<String>,
                    packName = name
                )
            }
        )
    }
}