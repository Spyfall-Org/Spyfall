package com.dangerfield.libraries.game

sealed class Pack(
    val name: String,
    val id: String,
    val version: Int,
    val languageCode: String,
    val type: PackType,
    val isPublic: Boolean,
    val owner: OwnerDetails,
    val isUserSaved: Boolean,
) {

    class LocationPack(
        val locations: List<Location>,
        name: String,
        id: String,
        version: Int,
        languageCode: String,
        isPublic: Boolean,
        owner: OwnerDetails,
        isUserSaved: Boolean,
    ) : Pack(
        name = name,
        id = id,
        version = version,
        languageCode = languageCode,
        type = PackType.Location,
        isPublic = isPublic,
        owner = owner,
        isUserSaved = isUserSaved
    )

    class CelebrityPack(
        val celebrities: List<Celebrity>,
        name: String,
        id: String,
        version: Int,
        languageCode: String,
        isPublic: Boolean,
        owner: OwnerDetails,
        isUserSaved: Boolean,
    ) : Pack(
        name = name,
        id = id,
        version = version,
        languageCode = languageCode,
        type = PackType.Celebrity,
        isPublic = isPublic,
        owner = owner,
        isUserSaved = isUserSaved
    )
}

enum class PackType {
    Location,
    Celebrity
}

sealed class OwnerDetails {
    data object MeUser : OwnerDetails()
    data class Community(val userId: String) : OwnerDetails()
    data object App : OwnerDetails()
}

class Location(
    val name: String,
    val roles: List<String>,
)

class Celebrity(
    val name: String,
)
