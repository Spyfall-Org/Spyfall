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
            hasUserPlayed = pack.hasMeUserPlayed
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
            hasUserPlayed = pack.hasMeUserPlayed
        )

        DbPackType.Custom -> Pack.CustomPack(
            id = pack.id,
            version = pack.version,
            languageCode = pack.languageCode,
            name = pack.name,
            items = items.map { item -> PackItem.Custom(item.name, item.roles) },
            isPublic = pack.isPublic,
            owner = when (pack.dbPackOwner) {
                App -> OwnerDetails.App
                User -> OwnerDetails.MeUser
                DbPackOwner.Community -> OwnerDetails.Community(pack.ownerId!!)
            },
            isUserSaved = pack.isUserSaved,
            hasUserPlayed = pack.hasMeUserPlayed
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

        DbPackType.Custom -> PackItem.Custom(
            name = name,
            roles = roles.orEmpty()
        )
    }
}

fun RemotePack.toPackEntity(hasMeUserPlayed: Boolean): PackEntity {
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
        isPendingSave = false,
        hasMeUserPlayed = hasMeUserPlayed
    )
}

fun RemotePack.toPack(
    hasMeUserPlayed: Boolean
): Pack<PackItem> {
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
            isUserSaved = false, // TODO this probably needs to be compared against a list of saved IDs
            hasUserPlayed = hasMeUserPlayed
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
            hasUserPlayed = hasMeUserPlayed
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
                    hasUserPlayed = false
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
                    hasUserPlayed = false
                )
            }
        }
        else -> throw IllegalArgumentException("Unknown pack type: ${this.type}")
    }
}