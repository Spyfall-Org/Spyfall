package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.Player
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import spyfallx.core.failure
import spyfallx.core.logOnError
import javax.inject.Inject

@AutoBind
class FirestoreGameDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val gameParser: GameParser
) : GameDataSource {

    override suspend fun setGame(game: Game) {
        db.collection(GAMES_COLLECTION_KEY)
            .document(game.accessCode)
            .set(game)
            .await()
    }

    override suspend fun getGame(accessCode: String): Try<Game> = db
        .collection(GAMES_COLLECTION_KEY)
        .document(accessCode)
        .get()
        .await()
        .let {
            val data = it.data ?: return GameError.GameNotFound.failure()
            gameParser.parseGame(it.id, data).logOnError()
        }

    override suspend fun removePlayer(accessCode: String, player: Player) {
        db.collection(GAMES_COLLECTION_KEY)
            .document(accessCode)
            .update(PLAYERS_FIELD_KEY, FieldValue.arrayRemove(player))
            .await()
    }

    override suspend fun addPlayer(accessCode: String, player: Player) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(PLAYERS_FIELD_KEY, FieldValue.arrayUnion(player))
            .await()
    }

    override suspend fun setLocation(accessCode: String, location: String) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(LOCATION_FIELD_KEY, location)
            .await()
    }

    override suspend fun endGame(accessCode: String) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode).delete().await()
    }

    override suspend fun setStarted(accessCode: String, started: Boolean) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(HAS_STARTED_FIELD_KEY, true)
            .await()
    }

    override suspend fun setPlayers(accessCode: String, list: List<Player>) {
        db.collection(GAMES_COLLECTION_KEY).document(accessCode)
            .update(PLAYERS_FIELD_KEY, list)
            .await()
    }

    companion object {
        const val IS_HOST_FIELD_KEY = "isHost"
        const val VIDEO_CALL_LINK_FIELD_KEY = "videoCallLink"
        const val GAMES_COLLECTION_KEY = "games"
        const val PLAYERS_FIELD_KEY = "players"
        const val LOCATION_FIELD_KEY = "location"
        const val PACK_NAMES_FIELD_KEY = "packNames"
        const val HAS_STARTED_FIELD_KEY = "hasStarted"
        const val TIME_LIMIT_MINS_FIELD_KEY = "timeLimitMins"
        const val STARTED_AT_FIELD_KEY = "startedAt"
        const val LOCATIONS_FIELD_KEY = "locations"
        const val USERNAME_FIELD_KEY = "userName"
        const val ROLE_FIELD_KEY = "role"
        const val IS_SPY_FIELD_KEY = "isSpy"
        const val USER_ID_FIELD_KEY = "id"
        const val VERSION_FIELD_KEY = "version"
    }
}
