package com.dangerfield.libraries.game.internal

import javax.inject.Inject

/**
 * On firebase a list of Players is stored as a map of id to player,
 * a Player is stored as a list of the field name to its value
 */
class PlayerSerializer @Inject constructor() {

    fun serializePlayers(players: List<BackendPlayer>): PlayerMaps = players.map {
        it.id to serializePlayer(it)
    }.toMap()

    fun serializePlayer(player: BackendPlayer): PlayerMap = mapOf(
        FirestoreBackendGameDataSource.USERNAME_FIELD_KEY to player.userName,
        FirestoreBackendGameDataSource.ROLE_FIELD_KEY to player.role,
        FirestoreBackendGameDataSource.IS_ODD_ONE_OUT_FIELD_KEY to player.isOddOneOut,
        FirestoreBackendGameDataSource.USER_ID_FIELD_KEY to player.id,
        FirestoreBackendGameDataSource.IS_HOST_FIELD_KEY to player.isHost,
        FirestoreBackendGameDataSource.VOTED_CORRECTLY_FIELD_KEY to player.votedCorrectly
    )

    fun deserializePlayers(players: PlayerMaps): List<BackendPlayer> = players.map { (id, player) ->
        deserializePlayer(player)
    }

    private fun deserializePlayer(map: PlayerMap): BackendPlayer = BackendPlayer(
        userName = map[FirestoreBackendGameDataSource.USERNAME_FIELD_KEY] as String,
        role = map[FirestoreBackendGameDataSource.ROLE_FIELD_KEY] as? String?,
        isOddOneOut = map[FirestoreBackendGameDataSource.IS_ODD_ONE_OUT_FIELD_KEY] as Boolean,
        id = map[FirestoreBackendGameDataSource.USER_ID_FIELD_KEY] as String,
        isHost = map[FirestoreBackendGameDataSource.IS_HOST_FIELD_KEY] as Boolean,
        votedCorrectly = map[FirestoreBackendGameDataSource.VOTED_CORRECTLY_FIELD_KEY] as? Boolean?
    )
}

typealias PlayerMaps = Map<String, PlayerMap>
typealias PlayerMap = Map<String, Any?>
