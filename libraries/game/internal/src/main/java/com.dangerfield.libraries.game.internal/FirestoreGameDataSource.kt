package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.Player
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import spyfallx.core.failure
import spyfallx.core.logOnError
import javax.inject.Inject

@AutoBind
class FirestoreGameDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val gameSerializer: GameMapSerializer,
    private val playerSerializer: PlayerSerializer
) : GameDataSource {

    override suspend fun setGame(game: Game) {
        db.collection(GAMES_COLLECTION_KEY)
            .document(game.accessCode)
            .set(gameSerializer.serializeGame(game))
            .await()
    }

    override suspend fun subscribeToGame(accessCode: String): Try<Flow<Game>> = Try {
        db.collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .snapshots(MetadataChanges.EXCLUDE)
            .mapNotNull {
                val data = it.data ?: return@mapNotNull null
                gameSerializer.deserializeGame(data)
                    .logOnError()
                    .getOrNull()
            }
    }

    override suspend fun getGame(accessCode: String): Try<Game> = db
        .collection(GAMES_COLLECTION_KEY)
        .document(accessCode)
        .get()
        .await()
        .let {
            val data = it.data ?: return GameError.GameNotFound.failure()
            gameSerializer.deserializeGame(data).logOnError()
        }

    /**
     * Players are stored as a map of id to player, so we need to remove the entry matching the id
     */
    override suspend fun removePlayer(accessCode: String, id: String) {
        db.collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .update(FieldPath.of(PLAYERS_FIELD_KEY, id), FieldValue.delete())
            .await()
    }

    override suspend fun updatePlayers(accessCode: String, list: List<Player>) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(PLAYERS_FIELD_KEY, playerSerializer.serializePlayers(list))
            .await()
    }.ignoreValue()

    override suspend fun addPlayer(accessCode: String, player: Player) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(
                FieldPath.of(PLAYERS_FIELD_KEY, player.id),
                playerSerializer.serializePlayer(player)
            )
            .await()
    }

    override suspend fun changeName(accessCode: String, newName: String, id: String) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(FieldPath.of(PLAYERS_FIELD_KEY, id, USERNAME_FIELD_KEY), newName)
            .await()
    }.ignoreValue()

    override suspend fun setLocation(accessCode: String, location: String) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(LOCATION_FIELD_KEY, location)
            .await()
    }

    override suspend fun endGame(accessCode: String) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode).delete().await()
    }

    override suspend fun setGameBeingStarted(accessCode: String, isBeingStarted: Boolean) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(IS_BEING_STARTED_KEY, isBeingStarted)
            .await()
    }.ignoreValue()

    override suspend fun setStartedAt(accessCode: String, startedAt: Long): Try<Unit> = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(STARTED_AT_FIELD_KEY, startedAt)
            .await()
    }.ignoreValue()

    override suspend fun setPlayerVotedCorrectly(
        accessCode: String,
        playerId: String,
        votedCorrectly: Boolean
    ) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(
                FieldPath.of(PLAYERS_FIELD_KEY, playerId, VOTED_CORRECTLY_FIELD_KEY),
                votedCorrectly
            )
            .await()
    }.ignoreValue()

    companion object {
        const val VOTED_CORRECTLY_FIELD_KEY = "votedCorrectly"
        const val ACCESS_CODE_FIELD_KEY = "accessCode"
        const val IS_HOST_FIELD_KEY = "isHost"
        const val VIDEO_CALL_LINK_FIELD_KEY = "videoCallLink"
        const val GAMES_COLLECTION_KEY = "games"
        const val PLAYERS_FIELD_KEY = "players"
        const val LOCATION_FIELD_KEY = "location"
        const val PACK_NAMES_FIELD_KEY = "packNames"
        const val IS_BEING_STARTED_KEY = "isBeingStarted"
        const val TIME_LIMIT_MINS_FIELD_KEY = "timeLimitMins"
        const val STARTED_AT_FIELD_KEY = "startedAt"
        const val LOCATIONS_FIELD_KEY = "locations"
        const val USERNAME_FIELD_KEY = "userName"
        const val ROLE_FIELD_KEY = "role"
        const val IS_ODD_ONE_OUT_FIELD_KEY = "isOddOneOut"
        const val USER_ID_FIELD_KEY = "id"
        const val VERSION_FIELD_KEY = "version"
    }
}
