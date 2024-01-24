package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameDataSourcError
import oddoneout.core.Try
import javax.inject.Inject

class GameMapSerializer @Inject constructor(
    private val playerSerializer: PlayerSerializer
) {

    fun deserializeGame(map: Map<String, Any>): Try<Game> {
        val version = (map[FirestoreGameDataSource.VERSION_FIELD_KEY] as? Number ?: 0).toInt()

        return if (version != CURRENT_GAME_MODEL_VERSION) {
            Try.Failure(
                GameDataSourcError.IncompatibleVersion(
                    isCurrentLower = version > CURRENT_GAME_MODEL_VERSION,
                    current = CURRENT_GAME_MODEL_VERSION,
                    other = version
                )
            )
        } else {
            Try {
                Game(
                    accessCode = map[FirestoreGameDataSource.ACCESS_CODE_FIELD_KEY] as String,
                    isBeingStarted = map[FirestoreGameDataSource.IS_BEING_STARTED_KEY] as Boolean,
                    locationName = map[FirestoreGameDataSource.LOCATION_FIELD_KEY] as String,
                    locationOptionNames = map[FirestoreGameDataSource.LOCATIONS_FIELD_KEY] as List<String>,
                    packNames = map[FirestoreGameDataSource.PACK_NAMES_FIELD_KEY] as List<String>,
                    players = (map[FirestoreGameDataSource.PLAYERS_FIELD_KEY] as PlayerMaps).let {
                        playerSerializer.deserializePlayers(it)
                    },
                    startedAt = map[FirestoreGameDataSource.STARTED_AT_FIELD_KEY] as? Long,
                    timeLimitMins = (map[FirestoreGameDataSource.TIME_LIMIT_MINS_FIELD_KEY] as Number).toInt(),
                    videoCallLink = map[FirestoreGameDataSource.VIDEO_CALL_LINK_FIELD_KEY] as? String?,
                    version = version,
                    lastActiveAt = map[FirestoreGameDataSource.LAST_ACTIVE_AT_FIELD_KEY] as? Long?
                )
            }
        }
    }

    fun serializeGame(game: Game): Map<String, Any?> = mapOf(
        FirestoreGameDataSource.ACCESS_CODE_FIELD_KEY to game.accessCode,
        FirestoreGameDataSource.IS_BEING_STARTED_KEY to game.isBeingStarted,
        FirestoreGameDataSource.LOCATION_FIELD_KEY to game.locationName,
        FirestoreGameDataSource.LOCATIONS_FIELD_KEY to game.locationOptionNames,
        FirestoreGameDataSource.PACK_NAMES_FIELD_KEY to game.packNames,
        FirestoreGameDataSource.PLAYERS_FIELD_KEY to playerSerializer.serializePlayers(game.players),
        FirestoreGameDataSource.STARTED_AT_FIELD_KEY to game.startedAt,
        FirestoreGameDataSource.TIME_LIMIT_MINS_FIELD_KEY to game.timeLimitMins,
        FirestoreGameDataSource.VIDEO_CALL_LINK_FIELD_KEY to game.videoCallLink,
        FirestoreGameDataSource.VERSION_FIELD_KEY to game.version
    )
}
