package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.Player
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.snapshots
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import oddoneout.core.Catching
import oddoneout.core.awaitCatching
import oddoneout.core.failure
import oddoneout.core.ignore
import oddoneout.core.ignoreValue
import oddoneout.core.logOnFailure
import oddoneout.core.mapTry
import oddoneout.core.toPrettyJson
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.time.Clock
import javax.inject.Inject

@AutoBind
class FirestoreBackendGameDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val gameSerializer: GameMapSerializer,
    private val playerSerializer: PlayerSerializer,
    private val clock: Clock,
    private val moshi: Moshi
) : BackendGameDataSource {
// TODO the data source should probably only be concerned with the backend game
    override suspend fun setGame(game: Game) {
        Catching {
            db.collection(GAMES_COLLECTION_KEY)
                .document(game.accessCode)
                .set(gameSerializer.serializeGame(game.toBackEndGame().withLastActiveAt()))
                .await()
        }
            .logOnFailure()
    }

    /**
     * Subscribes to the document and maps it to a game
     * Returns failure if game is not found or if there is an error
     */
    override suspend fun subscribeToGame(accessCode: String): Flow<Catching<BackendGame>> =
        db.collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .snapshots(MetadataChanges.EXCLUDE)
            .mapTry()
            .map {
                val document =
                    it.getOrNull() ?: return@map failure(GameError.CouldNotConnect(accessCode))
                val data = document.data ?: return@map failure(GameError.GameNotFound(accessCode))

                Timber.d("""
                    ----------------------------------------
                    Game update for access code: $accessCode
                    
                    ${
                        Catching {
                            moshi.toPrettyJson(data)
                        }.getOrNull() ?: "Could not parse data"
                    }
                    
                    ----------------------------------------
                """.trimIndent())

                gameSerializer.deserializeGame(data).logOnFailure()
            }

    override suspend fun getGame(accessCode: String): Catching<BackendGame> = Catching {
        db
            .collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .get()
            .awaitCatching()
            .map {
                val data = it.data ?: throw GameError.GameNotFound(accessCode)
                val gameTry = gameSerializer.deserializeGame(data)
                gameTry.getOrThrow()
            }
            .logOnFailure()
            .getOrThrow()
    }

    /**
     * Players are stored as a map of id to player, so we need to remove the entry matching the id
     */
    override suspend fun removePlayer(accessCode: String, id: String) = Catching {
        db.collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .activeUpdate(FieldPath.of(PLAYERS_FIELD_KEY, id), FieldValue.delete())
            .awaitCatching()
    }.ignoreValue()

    // TODO maybe this shoudl update the individual fields and not set the entire user
    // That user may change their name durring this.
    override suspend fun updatePlayers(accessCode: String, list: List<Player>) = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(PLAYERS_FIELD_KEY), playerSerializer.serializePlayers(list.toBackEndPlayers()))
            .awaitCatching()
    }.ignoreValue()

    override suspend fun addPlayer(accessCode: String, player: Player) = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(
                FieldPath.of(PLAYERS_FIELD_KEY, player.id),
                playerSerializer.serializePlayer(player.toBackEndPlayer()),
            )
            .awaitCatching()
    }
        .logOnFailure()
        .ignore()

    override suspend fun changeName(accessCode: String, newName: String, id: String) = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(PLAYERS_FIELD_KEY, id, USERNAME_FIELD_KEY), newName)
            .awaitCatching()
    }.ignoreValue()

    override suspend fun setHost(accessCode: String, id: String) = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(PLAYERS_FIELD_KEY, id, IS_HOST_FIELD_KEY), id)
            .awaitCatching()
    }.ignoreValue()

    override suspend fun setLocation(accessCode: String, location: String) = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(SECRET_ITEM_NAME_FIELD_KEY), location)
            .awaitCatching()
    }
        .logOnFailure()
        .ignore()

    override suspend fun delete(accessCode: String): Catching<Unit> = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode).delete().await()
    }.ignoreValue()

    override suspend fun setGameBeingStarted(accessCode: String, isBeingStarted: Boolean) = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(IS_BEING_STARTED_KEY), isBeingStarted)
            .awaitCatching()
    }.ignoreValue()

    override suspend fun setStartedAt(accessCode: String): Catching<Unit> = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(FieldPath.of(STARTED_AT_FIELD_KEY), clock.millis())
            .awaitCatching()
    }.ignoreValue()

    override suspend fun setPlayerVotedCorrectly(
        accessCode: String,
        playerId: String,
        votedCorrectly: Boolean
    ) = Catching {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .activeUpdate(
                FieldPath.of(PLAYERS_FIELD_KEY, playerId, VOTED_CORRECTLY_FIELD_KEY),
                votedCorrectly
            )
            .awaitCatching()
    }.ignoreValue()

    private fun BackendGame.withLastActiveAt(): BackendGame = copy(lastActiveAt = clock.millis())

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
        const val SECRET_ITEM_NAME_FIELD_KEY = "secretItemName"
        const val PACK_IDS_FIELD_KEY = "packIds"
        const val IS_BEING_STARTED_KEY = "isBeingStarted"
        const val TIME_LIMIT_SECONDS_FIELD_KEY = "timeLimitSeconds"
        const val STARTED_AT_FIELD_KEY = "startedAt"
        const val ITEMS_FIELD_KEY = "items"
        const val USERNAME_FIELD_KEY = "userName"
        const val LAST_ACTIVE_AT_FIELD_KEY = "lastActiveAt"
        const val ROLE_FIELD_KEY = "role"
        const val IS_ODD_ONE_OUT_FIELD_KEY = "isOddOneOut"
        const val USER_ID_FIELD_KEY = "id"
        const val VERSION_FIELD_KEY = "version"
        const val LANGUAGE_CODE_FIELD_KEY = "language"
        const val PACKS_VERSION_FIELD_KEY = "packsVersion"
    }
}
