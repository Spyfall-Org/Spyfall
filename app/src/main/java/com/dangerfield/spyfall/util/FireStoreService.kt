package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Player
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FireStoreService(private val db: FirebaseFirestore, private val constants: Constants) :
    GameService {

    override fun getGame(accessCode: String): Task<Game?> {
        val result: TaskCompletionSource<Game?> = TaskCompletionSource()
        db.collection(constants.games).document(accessCode).get().addOnSuccessListener { doc ->
            val game = if (doc != null && doc.exists()) {
                doc.toObject(Game::class.java)
            } else {
                null
            }
            result.setResult(game)

        }.addOnFailureListener {
            result.setException(it)
        }
        return result.task
    }

    override fun setGame(accessCode: String, game: Game): Task<Void> {
        return db.collection(constants.games).document(accessCode).set(game)
    }

    override fun removePlayer(accessCode: String, player: String): Task<Void> {
        val gameRef = db.collection(constants.games).document(accessCode)
        return gameRef.update(Constants.GameFields.playerList, FieldValue.arrayRemove(player))
    }

    override fun addPlayer(accessCode: String, player: String): Task<Void> {
        return db.collection(constants.games).document(accessCode)
            .update(Constants.GameFields.playerList, FieldValue.arrayUnion(player))
    }

    override fun updateChosenLocation(accessCode: String, newLocation: String): Task<Void> {
        return db.collection(constants.games).document(accessCode)
            .update(Constants.GameFields.chosenLocation, newLocation)
    }

    override fun endGame(accessCode: String): Task<Void> {
        return db.collection(constants.games).document(accessCode).delete()
    }

    override fun setStarted(accessCode: String, started: Boolean): Task<Void> {
        return db.collection(constants.games).document(accessCode)
            .update(Constants.GameFields.started, true)
    }

    override fun setPlayerList(accessCode: String, list: List<String>): Task<Void> {
        return db.collection(constants.games).document(accessCode)
            .update(Constants.GameFields.playerList, list)
    }

    override fun getPackDetails(): Task<List<List<String>>?> {

        val result: TaskCompletionSource<List<List<String>>?> = TaskCompletionSource()

        val list = mutableListOf<List<String>>()

        db.collection(constants.packs).get()
            .addOnSuccessListener { collection ->
                collection.documents.forEach { document ->
                    val pack = listOf(document.id) + document.data!!.keys.toList()
                    list.add(pack)
                }
                result.setResult(list)

            }.addOnFailureListener {
                result.setException(it)
            }

        return result.task
    }

    override fun incrementNumAndroidPlayers() {
        db.collection(Constants.StatisticsConstants.collection)
            .document(Constants.StatisticsConstants.document)
            .update(Constants.StatisticsConstants.num_android_players, FieldValue.increment(1))
    }

    override fun incrementNumGamesPlayed() {
        db.collection(Constants.StatisticsConstants.collection)
            .document(Constants.StatisticsConstants.document)
            .update(Constants.StatisticsConstants.num_games_played, FieldValue.increment(1))
    }

    override fun accessCodeExists(code: String): Task<Boolean> {
        val result: TaskCompletionSource<Boolean> = TaskCompletionSource()

        db.collection(constants.games).document(code).get().addOnSuccessListener {
            val exists = it != null && it.exists()
            result.setResult(exists)
        }.addOnFailureListener {
            result.setException(it)
        }
        return result.task
    }

    override suspend fun findRolesForLocationInPacks(
        packs: List<String>,
        chosenLocation: String
    ): Task<List<String>?> {
        val result: TaskCompletionSource<List<String>?> = TaskCompletionSource()

        try {
            val roles = packs.pmap {pack ->
                findRolesForLocationInPack(pack, chosenLocation).await()
            }.find { it != null } ?: listOf()
            result.setResult(roles)
        } catch (e : Exception) {
            result.setException(e)
        }

        return result.task
    }

    private fun findRolesForLocationInPack(
        pack: String,
        chosenLocation: String
    ): Task<List<String>?> {
        val result: TaskCompletionSource<List<String>?> = TaskCompletionSource()

        db.collection(constants.packs).document(pack).get().addOnSuccessListener {
            if (it != null && it.exists()) {
                val roles = it.get(chosenLocation) as ArrayList<String>?
                result.setResult(roles)
            } else {
                result.setResult(null)
            }
        }.addOnFailureListener {
            result.setException(it)
        }

        return result.task
    }

    override fun setPlayerObjectsList(accessCode: String, list: List<Player>): Task<Void> {
        return db.collection(constants.games).document(accessCode)
            .update(Constants.GameFields.playerObjectList, list.shuffled())
    }

    override fun getLocationsFromPack(pack: String, numberOfLocations: Int): Task<List<String>?> {
        val result: TaskCompletionSource<List<String>?> = TaskCompletionSource()

        db.collection(constants.packs).document(pack).get().addOnSuccessListener {packData ->
            val randomLocations =
                (packData.data?.toList()?.map { field -> field.first } ?: listOf()).shuffled()
                    .take(numberOfLocations)
            result.setResult(randomLocations)
        }.addOnFailureListener {
            result.setException(it)
        }
        return result.task
    }
}

