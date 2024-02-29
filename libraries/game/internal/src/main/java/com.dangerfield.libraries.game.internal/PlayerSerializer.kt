package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Player
import javax.inject.Inject

/**
 * On firebase a list of Players is stored as a map of id to player,
 * a Player is stored as a list of the field name to its value
 */
class PlayerSerializer @Inject constructor() {

    fun serializePlayers(players: List<Player>): PlayerMaps = players.map {
        it.id to serializePlayer(it)
    }.toMap()

    fun serializePlayer(player: Player): PlayerMap = mapOf(
        FirestoreGameDataSource.USERNAME_FIELD_KEY to player.userName,
        FirestoreGameDataSource.ROLE_FIELD_KEY to player.role,
        FirestoreGameDataSource.IS_ODD_ONE_OUT_FIELD_KEY to player.isOddOneOut,
        FirestoreGameDataSource.USER_ID_FIELD_KEY to player.id,
        FirestoreGameDataSource.IS_HOST_FIELD_KEY to player.isHost
    )

    fun deserializePlayers(players: PlayerMaps): List<Player> = players.map { (id, player) ->
        deserializePlayer(player)
    }

    fun deserializePlayer(map: PlayerMap): Player = Player(
        userName = map[FirestoreGameDataSource.USERNAME_FIELD_KEY] as String,
        role = map[FirestoreGameDataSource.ROLE_FIELD_KEY] as? String?,
        isOddOneOut = map[FirestoreGameDataSource.IS_ODD_ONE_OUT_FIELD_KEY] as Boolean,
        id = map[FirestoreGameDataSource.USER_ID_FIELD_KEY] as String,
        isHost = map[FirestoreGameDataSource.IS_HOST_FIELD_KEY] as Boolean,
        votedCorrectly = map[FirestoreGameDataSource.VOTED_CORRECTLY_FIELD_KEY] as? Boolean?
    )
}

typealias PlayerMaps = Map<String, PlayerMap>
typealias PlayerMap = Map<String, Any?>
