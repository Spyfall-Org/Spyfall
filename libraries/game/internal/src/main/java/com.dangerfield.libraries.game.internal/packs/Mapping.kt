package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.game.Celebrity
import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.storage.DbPackOwner
import com.dangerfield.libraries.game.storage.DbPackOwner.App
import com.dangerfield.libraries.game.storage.DbPackOwner.User
import com.dangerfield.libraries.game.storage.DbPackType
import com.dangerfield.libraries.game.storage.PackEntity
import com.dangerfield.libraries.game.storage.PackWithItems

fun PackWithItems.toPack(): Pack {
    return when (pack.type) {
        DbPackType.Location -> Pack.LocationPack(
            id = pack.id,
            version = pack.version,
            languageCode = pack.languageCode,
            name = pack.name,
            locations = items.map { item ->
                Location(
                    name = item.name,
                    roles = item.roles.orEmpty()
                )
            },
            isPublic = pack.isPublic,
            owner = when (pack.dbPackOwner) {
                App -> OwnerDetails.App
                User -> OwnerDetails.MeUser
                DbPackOwner.Community -> OwnerDetails.Community(pack.ownerId!!)
            },
            isUserSaved = pack.isUserSaved,
        )

        DbPackType.Celebrity -> Pack.CelebrityPack(
            id = pack.id,
            version = pack.version,
            languageCode = pack.languageCode,
            name = pack.name,
            celebrities = items.map { item -> Celebrity(item.name) },
            isPublic = pack.isPublic,
            owner = when (pack.dbPackOwner) {
                App -> OwnerDetails.App
                User -> OwnerDetails.MeUser
                DbPackOwner.Community -> OwnerDetails.Community(pack.ownerId!!)
            },
            isUserSaved = pack.isUserSaved,
        )
    }
}

fun RemotePack.toPackEntity(): PackEntity {
    val it = this
    return PackEntity(
        id = it.id,
        version = it.version,
        languageCode = it.languageCode,
        groupId = it.groupId,
        dbPackOwner = App,
        isUserSaved = false,
        name = it.name,
        type = when (it.type) {
            RemotePackConstants.PACK_TYPE_LOCATION -> DbPackType.Location
            RemotePackConstants.PACK_TYPE_CELEBRITY -> DbPackType.Celebrity
            else -> throw IllegalArgumentException("Unknown pack type: ${it.type}")
        },
        isPublic = it.isPublic,
        ownerId = it.ownerId,
    )
}

fun RemotePack.toPack(): Pack {
    val it = this
    return when (it.type) {
        RemotePackConstants.PACK_TYPE_LOCATION -> Pack.LocationPack(
            id = it.id,
            version = it.version,
            languageCode = it.languageCode,
            name = it.name,
            locations = it.packItems.map { item ->
                Location(
                    name = item.name,
                    roles = item.roles.orEmpty()
                )
            },
            isPublic = it.isPublic,
            owner = OwnerDetails.App,
            isUserSaved = false,
        )

        RemotePackConstants.PACK_TYPE_CELEBRITY -> Pack.CelebrityPack(
            id = it.id,
            version = it.version,
            languageCode = it.languageCode,
            name = it.name,
            celebrities = it.packItems.map { item -> Celebrity(item.name) },
            isPublic = it.isPublic,
            owner = OwnerDetails.App,
            isUserSaved = false,
        )

        else -> throw IllegalArgumentException("Unknown pack type: ${it.type}")
    }
}