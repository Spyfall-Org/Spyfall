package com.dangerfield.libraries.game.internal.packs


import com.dangerfield.libraries.game.internal.packs.RemotePackConstants.PACK_TYPE_CELEBRITY
import com.dangerfield.libraries.game.internal.packs.RemotePackConstants.PACK_TYPE_LOCATION
import oddoneout.core.Catching
import javax.inject.Inject

class CustomPackParser @Inject constructor() {

    fun parsePack(
        data: Map<String, Any>,
        isPublic: Boolean
    ): Catching<RemotePack> = Catching {
        val type = data["type"] as String

        RemotePack(
            name = data["name"] as String,
            id = data["id"] as String,
            groupId = null,
            version = data["version"] as Int,
            languageCode = data["languageCode"] as String,
            type = type,
            saves = data["saves"] as? Int ?: 0,
            isPublic = isPublic,
            ownerId = data["ownerId"] as? String,
            packItems = when (type) {
                PACK_TYPE_LOCATION -> {
                    val locations = data["locations"] as List<Map<String, Any>>
                    locations.map { location ->
                        RemotePackItem(
                            name = location["name"] as String,
                            roles = location["roles"] as List<String>,
                        )
                    }
                }

                PACK_TYPE_CELEBRITY -> {
                    val celebrities = data["celebrities"] as List<String>
                    celebrities.map { name ->
                        RemotePackItem(
                            name = name,
                            roles = null
                        )
                    }
                }

                else -> throw IllegalArgumentException("Unknown pack type: $type")
            }
        )
    }
}