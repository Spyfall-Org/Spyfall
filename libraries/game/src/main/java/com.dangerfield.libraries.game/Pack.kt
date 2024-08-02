package com.dangerfield.libraries.game

open class Pack<T: PackItem>(
    val name: String,
    val id: String,
    val version: Int,
    val languageCode: String,
    val type: PackType,
    val isPublic: Boolean,
    val owner: OwnerDetails,
    val isUserSaved: Boolean,
    val items: List<T>
) {

    class LocationPack(
        val locations: List<PackItem.Location>,
        name: String,
        id: String,
        version: Int,
        languageCode: String,
        isPublic: Boolean,
        owner: OwnerDetails,
        isUserSaved: Boolean,
    ) : Pack<PackItem>(
        name = name,
        id = id,
        version = version,
        languageCode = languageCode,
        type = PackType.Location,
        isPublic = isPublic,
        owner = owner,
        isUserSaved = isUserSaved,
        items = locations
    )

    class CelebrityPack(
        val celebrities: List<PackItem.Celebrity>,
        name: String,
        id: String,
        version: Int,
        languageCode: String,
        isPublic: Boolean,
        owner: OwnerDetails,
        isUserSaved: Boolean,
    ) : Pack<PackItem>(
        name = name,
        id = id,
        version = version,
        languageCode = languageCode,
        type = PackType.Celebrity,
        isPublic = isPublic,
        owner = owner,
        isUserSaved = isUserSaved,
        items = celebrities
    )

    class CustomPack(
        name: String,
        id: String,
        version: Int,
        languageCode: String,
        isPublic: Boolean,
        owner: OwnerDetails,
        isUserSaved: Boolean,
        items: List<PackItem.Custom>
    ) : Pack<PackItem.Custom>(
        name = name,
        id = id,
        version = version,
        languageCode = languageCode,
        type = PackType.Custom,
        isPublic = isPublic,
        owner = owner,
        isUserSaved = isUserSaved,
        items = items
    )
}

enum class PackType {
    Location,
    Celebrity,
    Custom
}

sealed class OwnerDetails {
    data object MeUser : OwnerDetails()
    data class Community(val userId: String) : OwnerDetails()
    data object App : OwnerDetails()
}

sealed class PackItem(
    val name: String,
    val roles: List<String>?
) {
    class Location(
        name: String,
        roles: List<String>,
    ) : PackItem(name, roles)

    class Celebrity(
        name: String,
    ) : PackItem(name, null)

    class Custom(
        name: String,
        roles: List<String>?
    ) : PackItem(name, roles)
}
