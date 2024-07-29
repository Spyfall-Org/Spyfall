package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameDataSourcError
import oddoneout.core.Catching
import javax.inject.Inject

class GameMapSerializer @Inject constructor(
    private val playerSerializer: PlayerSerializer
) {

    fun deserializeGame(map: Map<String, Any>): Catching<Game> {
        val version = (map[FirestoreGameDataSource.VERSION_FIELD_KEY] as? Number ?: 0).toInt()

        return if (version != CURRENT_GAME_MODEL_VERSION) {
            Catching.failure(
                GameDataSourcError.IncompatibleVersion(
                    isCurrentLower = version > CURRENT_GAME_MODEL_VERSION,
                    current = CURRENT_GAME_MODEL_VERSION,
                    other = version
                )
            )
        } else {
            Catching {
                Game(
                    accessCode = map[FirestoreGameDataSource.ACCESS_CODE_FIELD_KEY] as String,
                    isBeingStarted = map[FirestoreGameDataSource.IS_BEING_STARTED_KEY] as Boolean,
                    secret = map[FirestoreGameDataSource.SECRET_ITEM_FIELD_KEY] as String,
                    secretOptions = map[FirestoreGameDataSource.ITEMS_FIELD_KEY] as List<String>,
                    packIds = map[FirestoreGameDataSource.PACK_IDS_FIELD_KEY] as List<String>,
                    players = (map[FirestoreGameDataSource.PLAYERS_FIELD_KEY] as PlayerMaps).let {
                        playerSerializer.deserializePlayers(it)
                    },
                    startedAt = map[FirestoreGameDataSource.STARTED_AT_FIELD_KEY] as? Long,
                    timeLimitMins = (map[FirestoreGameDataSource.TIME_LIMIT_MINS_FIELD_KEY] as Number).toInt(),
                    videoCallLink = map[FirestoreGameDataSource.VIDEO_CALL_LINK_FIELD_KEY] as? String?,
                    version = version,
                    lastActiveAt = map[FirestoreGameDataSource.LAST_ACTIVE_AT_FIELD_KEY] as? Long?,
                    languageCode = map[FirestoreGameDataSource.LANGUAGE_CODE_FIELD_KEY] as? String? ?: "en",
                    packsVersion = (map[FirestoreGameDataSource.PACKS_VERSION_FIELD_KEY] as? Number)?.toInt() ?: 0
                )
            }
        }
    }

    fun serializeGame(game: Game): Map<String, Any?> = mapOf(
        FirestoreGameDataSource.ACCESS_CODE_FIELD_KEY to game.accessCode,
        FirestoreGameDataSource.IS_BEING_STARTED_KEY to game.isBeingStarted,
        FirestoreGameDataSource.SECRET_ITEM_FIELD_KEY to game.secret,
        FirestoreGameDataSource.ITEMS_FIELD_KEY to game.secretOptions,
        FirestoreGameDataSource.PACK_IDS_FIELD_KEY to game.packIds,
        FirestoreGameDataSource.PLAYERS_FIELD_KEY to playerSerializer.serializePlayers(game.players),
        FirestoreGameDataSource.STARTED_AT_FIELD_KEY to game.startedAt,
        FirestoreGameDataSource.TIME_LIMIT_MINS_FIELD_KEY to game.timeLimitMins,
        FirestoreGameDataSource.VIDEO_CALL_LINK_FIELD_KEY to game.videoCallLink,
        FirestoreGameDataSource.VERSION_FIELD_KEY to game.version,
        FirestoreGameDataSource.PACKS_VERSION_FIELD_KEY to game.packsVersion,
        FirestoreGameDataSource.LANGUAGE_CODE_FIELD_KEY to game.languageCode,
        FirestoreGameDataSource.LAST_ACTIVE_AT_FIELD_KEY to game.lastActiveAt,
    )
}
