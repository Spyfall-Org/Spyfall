package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.coreflowroutines.mapTry
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameDataSourcError
import com.dangerfield.libraries.game.GameDataSourcError.CouldNotConnect
import com.dangerfield.libraries.game.GameDataSourcError.GameNotFound
import com.dangerfield.libraries.game.Player
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.Try
import oddoneout.core.failure
import oddoneout.core.logOnError
import oddoneout.core.tryAwait
import java.time.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.nanoseconds

@AutoBind
class FirestoreGameDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val gameSerializer: GameMapSerializer,
    private val playerSerializer: PlayerSerializer,
    private val clock: Clock
) : GameDataSource {

    override suspend fun setGame(game: Game) {
        Try {
            db.collection(GAMES_COLLECTION_KEY)
                .document(game.accessCode)
                .set(gameSerializer.serializeGame(game.withLastActiveAt()))
                .await()
        }
            .logOnError()
    }

    /**
     * Subscribes to the document and maps it to a game
     * Returns failure if game is not found or if there is an error
     */
    override suspend fun subscribeToGame(accessCode: String): Flow<Try<Game>> =
        db.collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .snapshots(MetadataChanges.EXCLUDE)
            .mapTry()
            .map {
                val document =
                    it.getOrNull() ?: return@map CouldNotConnect(accessCode)
                        .failure()
                val data = document.data ?: return@map GameNotFound(accessCode)
                    .failure()
                gameSerializer.deserializeGame(data).logOnError()
            }

    override suspend fun getGame(accessCode: String): Try<Game> = Try {
        db
            .collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .get()
            .tryAwait()
            .map {
                val data = it.data ?: throw GameNotFound(accessCode)
                val gameTry = gameSerializer.deserializeGame(data).logOnError()
                gameTry.getOrThrow()
            }.getOrThrow()
    }

    /**
     * Players are stored as a map of id to player, so we need to remove the entry matching the id
     */
    override suspend fun removePlayer(accessCode: String, id: String) = Try {
        db.collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .activeUpdate(FieldPath.of(PLAYERS_FIELD_KEY, id), FieldValue.delete())
            .tryAwait()
    }.ignoreValue()

    // TODO maybe this shoudl update the individual fields and not set the entire user
    // That user may change their name durring this.
    override suspend fun updatePlayers(accessCode: String, list: List<Player>) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(PLAYERS_FIELD_KEY), playerSerializer.serializePlayers(list))
            .tryAwait()
    }.ignoreValue()

    override suspend fun addPlayer(accessCode: String, player: Player) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(
                FieldPath.of(PLAYERS_FIELD_KEY, player.id),
                playerSerializer.serializePlayer(player),
            )
            .tryAwait()
    }
        .logOnError()
        .ignoreEverything()

    override suspend fun changeName(accessCode: String, newName: String, id: String) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(PLAYERS_FIELD_KEY, id, USERNAME_FIELD_KEY), newName)
            .tryAwait()
    }.ignoreValue()

    override suspend fun setLocation(accessCode: String, location: String) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(LOCATION_FIELD_KEY), location)
            .tryAwait()
    }
        .logOnError()
        .ignoreEverything()

    override suspend fun delete(accessCode: String): Try<Unit> = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode).delete().await()
    }.ignoreValue()

    override suspend fun setGameBeingStarted(accessCode: String, isBeingStarted: Boolean) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(IS_BEING_STARTED_KEY), isBeingStarted)
            .tryAwait()
    }.ignoreValue()

    override suspend fun setStartedAt(accessCode: String): Try<Unit> = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(STARTED_AT_FIELD_KEY), clock.millis())
            .tryAwait()
    }.ignoreValue()

    override suspend fun setPlayerVotedCorrectly(
        accessCode: String,
        playerId: String,
        votedCorrectly: Boolean
    ) = Try {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(
                FieldPath.of(PLAYERS_FIELD_KEY, playerId, VOTED_CORRECTLY_FIELD_KEY),
                votedCorrectly
            )
            .tryAwait()
    }.ignoreValue()

    private fun Game.withLastActiveAt(): Game = copy(lastActiveAt = clock.millis())

    private fun DocumentReference.activeUpdate(fieldPath: FieldPath, value: Any?) =
        update(
            fieldPath, value, FieldPath.of(LAST_ACTIVE_AT_FIELD_KEY), clock.millis()
        )

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
        const val LAST_ACTIVE_AT_FIELD_KEY = "lastActiveAt"
        const val ROLE_FIELD_KEY = "role"
        const val IS_ODD_ONE_OUT_FIELD_KEY = "isOddOneOut"
        const val USER_ID_FIELD_KEY = "id"
        const val VERSION_FIELD_KEY = "version"
    }
}
