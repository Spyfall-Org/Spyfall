package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.game.PackItem.Celebrity
import com.dangerfield.libraries.game.PackItem.Location
import com.dangerfield.libraries.game.storage.DbPackOwner
import com.dangerfield.libraries.game.storage.DbPackOwner.App
import com.dangerfield.libraries.game.storage.DbPackOwner.User
import com.dangerfield.libraries.game.storage.DbPackType
import com.dangerfield.libraries.game.storage.PackEntity
import com.dangerfield.libraries.game.storage.PackItemEntity
import com.dangerfield.libraries.game.storage.PackWithItems

fun PackWithItems.toPack(): Pack<PackItem> {
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

fun RemotePack.toItemEntities(): List<PackItemEntity> {
    return packItems.map { item ->
        PackItemEntity(
            name = item.name,
            roles = item.roles,
            packId = id,
            languageCode = this.languageCode,
        )
    }
}

fun PackItemEntity.toPackItem(pack: PackEntity): PackItem {

    return when (pack.type) {
        DbPackType.Location -> Location(
            name = name,
            roles = roles.orEmpty()
        )

        DbPackType.Celebrity -> Celebrity(
            name = name
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

fun RemotePack.toPack(): Pack<PackItem> {
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

fun JsonPacks.toPacks(): List<Pack<PackItem>> {
    return when(this.type) {
        JsonFallbackLocationPacksDataSource.PACK_TYPE_LOCATION -> {
            this.packs.map { pack ->
                Pack.LocationPack(
                    id = this.languageCode + this.version + pack.name,
                    version = this.version,
                    languageCode = this.languageCode,
                    name = pack.name,
                    locations = pack.locations.map { jsonLocation ->
                        Location(
                            name = jsonLocation.name,
                            roles = jsonLocation.roles
                        )
                    },
                    isPublic = false,
                    owner = OwnerDetails.App,
                    isUserSaved = false,
                )
            }
        }
        JsonFallbackLocationPacksDataSource.PACK_TYPE_CELEBRITY -> {
            this.packs.map { pack ->
                Pack.CelebrityPack(
                    id = this.languageCode + this.version + pack.name,
                    version = this.version,
                    languageCode = this.languageCode,
                    name = pack.name,
                    celebrities = pack.locations.map { jsonLocation ->
                        Celebrity(
                            name = jsonLocation.name
                        )
                    },
                    isPublic = false,
                    owner = OwnerDetails.App,
                    isUserSaved = false,
                )
            }
        }
        else -> throw IllegalArgumentException("Unknown pack type: ${this.type}")
    }
}