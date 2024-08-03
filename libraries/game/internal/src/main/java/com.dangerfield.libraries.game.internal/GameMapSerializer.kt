package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.GameError
import oddoneout.core.Catching
import javax.inject.Inject

class GameMapSerializer @Inject constructor(
    private val playerSerializer: PlayerSerializer
) {

    fun deserializeGame(map: Map<String, Any>): Catching<BackendGame> {
        val version = (map[FirestoreBackendGameDataSource.VERSION_FIELD_KEY] as? Number ?: 0).toInt()

        return if (version != CURRENT_GAME_MODEL_VERSION) {
            Catching.failure(
                GameError.IncompatibleVersion(
                    isCurrentLower = version > CURRENT_GAME_MODEL_VERSION,
                    current = CURRENT_GAME_MODEL_VERSION,
                    other = version
                )
            )
        } else {
            Catching {
                BackendGame(
                    accessCode = map[FirestoreBackendGameDataSource.ACCESS_CODE_FIELD_KEY] as String,
                    isBeingStarted = map[FirestoreBackendGameDataSource.IS_BEING_STARTED_KEY] as Boolean,
                     secretItemName = map[FirestoreBackendGameDataSource.SECRET_ITEM_NAME_FIELD_KEY] as String,
                    items = map[FirestoreBackendGameDataSource.ITEMS_FIELD_KEY] as List<String>,
                    packIds = map[FirestoreBackendGameDataSource.PACK_IDS_FIELD_KEY] as List<String>,
                    players = (map[FirestoreBackendGameDataSource.PLAYERS_FIELD_KEY] as PlayerMaps).let {
                        playerSerializer.deserializePlayers(it)
                    },
                    startedAt = map[FirestoreBackendGameDataSource.STARTED_AT_FIELD_KEY] as? Long,
                    timeLimitSeconds = (map[FirestoreBackendGameDataSource.TIME_LIMIT_SECONDS_FIELD_KEY] as Number).toInt(),
                    videoCallLink = map[FirestoreBackendGameDataSource.VIDEO_CALL_LINK_FIELD_KEY] as? String?,
                    gameVersion = version,
                    lastActiveAt = map[FirestoreBackendGameDataSource.LAST_ACTIVE_AT_FIELD_KEY] as? Long?,
                    languageCode = map[FirestoreBackendGameDataSource.LANGUAGE_CODE_FIELD_KEY] as? String? ?: "en",
                    packsVersion = (map[FirestoreBackendGameDataSource.PACKS_VERSION_FIELD_KEY] as? Number)?.toInt() ?: 0
                )
            }
        }
    }

    fun serializeGame(game: BackendGame): Map<String, Any?> = mapOf(
        FirestoreBackendGameDataSource.ACCESS_CODE_FIELD_KEY to game.accessCode,
        FirestoreBackendGameDataSource.IS_BEING_STARTED_KEY to game.isBeingStarted,
        FirestoreBackendGameDataSource.SECRET_ITEM_NAME_FIELD_KEY to game.secretItemName,
        FirestoreBackendGameDataSource.ITEMS_FIELD_KEY to game.items,
        FirestoreBackendGameDataSource.PACK_IDS_FIELD_KEY to game.packIds,
        FirestoreBackendGameDataSource.PLAYERS_FIELD_KEY to playerSerializer.serializePlayers(game.players),
        FirestoreBackendGameDataSource.STARTED_AT_FIELD_KEY to game.startedAt,
        FirestoreBackendGameDataSource.TIME_LIMIT_SECONDS_FIELD_KEY to game.timeLimitSeconds,
        FirestoreBackendGameDataSource.VIDEO_CALL_LINK_FIELD_KEY to game.videoCallLink,
        FirestoreBackendGameDataSource.VERSION_FIELD_KEY to game.gameVersion,
        FirestoreBackendGameDataSource.PACKS_VERSION_FIELD_KEY to game.packsVersion,
        FirestoreBackendGameDataSource.LANGUAGE_CODE_FIELD_KEY to game.languageCode,
        FirestoreBackendGameDataSource.LAST_ACTIVE_AT_FIELD_KEY to game.lastActiveAt,
    )
}
