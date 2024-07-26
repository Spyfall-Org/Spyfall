package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.game.internal.packs.RemotePackConstants.PACK_TYPE_CELEBRITY
import com.dangerfield.libraries.game.internal.packs.RemotePackConstants.PACK_TYPE_LOCATION
import oddoneout.core.Catching
import javax.inject.Inject

class AppPackParser @Inject constructor() {

    fun parsePacks(
        data: Map<String, Any>,
        version: Int,
        languageCode: String,
    ) : Catching<List<RemotePack>> = Catching {
        val packs = data["packs"] as List<Map<String, Any>>
        val groupId = data["groupId"] as String

        packs.map {
            val packName = it["name"] as String
            val packId = it["id"] as String
            val packType = it["type"] as String

            RemotePack(
                name = packName,
                id = packId,
                groupId = groupId,
                version = version,
                languageCode = languageCode,
                type =  packType,
                isPublic = true,
                ownerId = null,
                packItems = when(packType) {
                    PACK_TYPE_LOCATION -> {
                        val locations = it["locations"] as List<Map<String, Any>>
                        locations.map { location ->
                            RemotePackItem(
                                name = location["name"] as String,
                                roles = location["roles"] as List<String>,
                            )
                        }
                    }
                    PACK_TYPE_CELEBRITY -> {
                        val celebrities = it["celebrities"] as List<String>
                        celebrities.map { name ->
                            RemotePackItem(
                                name = name,
                                roles = null
                            )
                        }
                    }
                    else -> throw IllegalArgumentException("Unknown pack type: $packType")
                }
            )
        }
    }
}