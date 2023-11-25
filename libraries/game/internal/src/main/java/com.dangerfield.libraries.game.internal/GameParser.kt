package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.Player
import spyfallx.core.Try
import javax.inject.Inject

class GameParser @Inject constructor() {

    fun parseGame(map: Map<String, Any>): Try<Game> {
        val version = (map[FirestoreGameDataSource.VERSION_FIELD_KEY] as? Number ?: 0).toInt()

        return if (version != CURRENT_GAME_MODEL_VERSION) {
            Try.Failure(
                GameError.IncompatibleVersion(
                    isCurrentLower = version > CURRENT_GAME_MODEL_VERSION,
                    current = CURRENT_GAME_MODEL_VERSION,
                    other = version
                )
            )
        } else {
            Try {
                Game(
                    location = map[FirestoreGameDataSource.LOCATION_FIELD_KEY] as String,
                    packNames = map[FirestoreGameDataSource.PACK_NAMES_FIELD_KEY] as List<String>,
                    hasStarted = map[FirestoreGameDataSource.HAS_STARTED_FIELD_KEY] as Boolean,
                    players = (map[FirestoreGameDataSource.PLAYERS_FIELD_KEY] as PlayerMaps).toPlayers(),
                    timeLimitMins = (map[FirestoreGameDataSource.TIME_LIMIT_MINS_FIELD_KEY] as Number).toInt(),
                    startedAt = map[FirestoreGameDataSource.STARTED_AT_FIELD_KEY] as Long,
                    locations = map[FirestoreGameDataSource.LOCATIONS_FIELD_KEY] as List<String>,
                    version = version
                )
            }
        }
    }
}

typealias PlayerMaps = List<PlayerMap>
typealias PlayerMap = Map<String, Any>

fun PlayerMaps.toPlayers() = this.map {
    Player(
        userName = it[FirestoreGameDataSource.USERNAME_FIELD_KEY] as String,
        role = it[FirestoreGameDataSource.ROLE_FIELD_KEY] as? String?,
        isSpy = it[FirestoreGameDataSource.IS_SPY_FIELD_KEY] as Boolean,
        id = it[FirestoreGameDataSource.USER_ID_FIELD_KEY] as String
    )
}