package com.dangerfield.libraries.game

open class Pack<T : PackItem>(
    val packName: String,
    val packId: String,
    val packVersion: Int,
    val packLanguageCode: String,
    val packType: PackType,
    val packIsPublic: Boolean,
    val packOwner: OwnerDetails,
    val packIsUserSaved: Boolean,
    val packItems: List<T>,
    val packHasUserPlayed: Boolean
) {

    data class LocationPack(
        val locations: List<PackItem.Location>,
        val name: String,
        val id: String,
        val version: Int,
        val languageCode: String,
        val isPublic: Boolean,
        val owner: OwnerDetails,
        val isUserSaved: Boolean,
        val hasUserPlayed: Boolean,
    ) : Pack<PackItem>(
        packName = name,
        packId = id,
        packVersion = version,
        packLanguageCode = languageCode,
        packType = PackType.Location,
        packIsPublic = isPublic,
        packOwner = owner,
        packIsUserSaved = isUserSaved,
        packItems = locations,
        packHasUserPlayed = hasUserPlayed
    ) {

        companion object {
            val Fakes = LocationPackFakes
        }
    }

    data class CelebrityPack(
        val celebrities: List<PackItem.Celebrity>,
        val name: String,
        val id: String,
        val version: Int,
        val languageCode: String,
        val isPublic: Boolean,
        val owner: OwnerDetails,
        val isUserSaved: Boolean,
        val hasUserPlayed: Boolean,
    ) : Pack<PackItem>(
        packName = name,
        packId = id,
        packVersion = version,
        packLanguageCode = languageCode,
        packType = PackType.Celebrity,
        packIsPublic = isPublic,
        packOwner = owner,
        packIsUserSaved = isUserSaved,
        packItems = celebrities,
        packHasUserPlayed = hasUserPlayed
    )

    data class CustomPack(
        val name: String,
        val id: String,
        val version: Int,
        val languageCode: String,
        val isPublic: Boolean,
        val owner: OwnerDetails,
        val isUserSaved: Boolean,
        val items: List<PackItem.Custom>,
        val hasUserPlayed: Boolean,
    ) : Pack<PackItem>(
        packName = name,
        packId = id,
        packVersion = version,
        packLanguageCode = languageCode,
        packType = PackType.Custom,
        packIsPublic = isPublic,
        packOwner = owner,
        packIsUserSaved = isUserSaved,
        packItems = items,
        packHasUserPlayed = hasUserPlayed
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
