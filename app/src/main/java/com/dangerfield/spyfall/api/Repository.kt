package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.joinGame.JoinGameError
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.newGame.NewGameError
import com.dangerfield.spyfall.newGame.PackDetailsError
import com.dangerfield.spyfall.util.Connectivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class Repository(override var db: FirebaseFirestore) : GameRepository() {


    /**
     * job used to tie all coroutines to in order to cancel
     */
    private var job: Job = Job()

    /**
     * Holds info for current game being played. Set when joining game
     * nulled when leaving or ending game
     */
    override var currentSession: CurrentSession? = null

    /**
     * Set by Receiver to determine network connection
     */
    var hasNetworkConnection: Boolean = false

    /**
     * Creates a game node on firebase
     * Returns Access code to that node
     */
    override fun createGame(username: String, timeLimit: Long, chosenPacks: List<String>): LiveData<Resource<Unit, NewGameError>> {
        var result = MutableLiveData<Resource<Unit, NewGameError>>()

        if(!Connectivity.isOnline){
            result.value  = Resource.Error(error = NewGameError.NETWORK_ERROR)
        } else {
            CoroutineScope(IO + job).launch {
                val accessCode = generateAccessCode()
                val gameLocations = getGameLocations(chosenPacks as ArrayList<String>)
                val game = Game(
                    gameLocations.random(),
                    chosenPacks,
                    false,
                    arrayListOf(username),
                    arrayListOf(),
                    timeLimit,
                    gameLocations
                )

                val gameRef = db.collection(Collections.games).document(accessCode)

                gameRef.set(game).addOnSuccessListener {
                    result.value = Resource.Success(Unit)
                    currentSession = CurrentSession(accessCode, username).withListener(gameRef)
                }.addOnFailureListener {
                    result.value  = Resource.Error(error = NewGameError.UNKNOWN_ERROR)
                }
            }
        }

        return result
    }

    /*
retrieves location list to show in game and sets that value on firebase
also picks a random location from list to be the chosen location
 */
    suspend fun getGameLocations(chosenPacks: ArrayList<String>) : ArrayList<String> {
        val locationList = arrayListOf<String>()
        //dictates how the number locations we grab from each pack
        val numberFromEach = when(chosenPacks.size) {
            1 -> 14
            2 -> 7
            3 -> 5
            else -> 14
        }

        chosenPacks.forEach {pack ->
            val packData = db.collection("packs").document(pack).get().await()
            val randomLocations = (packData.data?.toList()?.map { field -> field.first } ?: listOf()).shuffled().take(numberFromEach)
            locationList.addAll(randomLocations)
        }

        return locationList.take(14) as ArrayList<String>
    }

    /**
     * Adds user name to games player list (no need for checks)
     * Adds listener to firebase to update game
     */
    override fun joinGame(accessCode: String, username: String): LiveData<Resource<Unit, JoinGameError>>  {
        val result = MutableLiveData<Resource<Unit, JoinGameError>>()

        if(!Connectivity.isOnline){
            result.value  = Resource.Error(error = JoinGameError.NETWORK_ERROR)
        } else {
            db.collection(Collections.games).document(accessCode).get().addOnSuccessListener { game ->

                if(game.exists()){
                    val list = (game["playerList"] as ArrayList<String>)

                    when {
                        list.size >= 8 ->
                            result.value = Resource.Error(error = JoinGameError.GAME_HAS_MAX_PLAYERS)

                        game["started"] == true ->
                            result.value = Resource.Error(error = JoinGameError.GAME_HAS_STARTED)

                        list.contains(username) ->
                            result.value = Resource.Error(error = JoinGameError.NAME_TAKEN)

                        else -> {
                            addPlayer(username, accessCode).addOnSuccessListener {
                                Crashlytics.log("Player: \"$username\" has joined $accessCode")
                                val gameRef: DocumentReference = db.collection("games").document(accessCode)
                                currentSession = CurrentSession(accessCode, username).withListener(gameRef)
                                result.value = Resource.Success(Unit)
                            }.addOnFailureListener {
                                Crashlytics.log("Player: \"$username\" was unable to join $accessCode")
                                result.value = Resource.Error(error = JoinGameError.COULD_NOT_JOIN)
                            }
                        }
                    }
                }else{
                    result.value = Resource.Error(error = JoinGameError.GAME_DOES_NOT_EXIST)
                }
            }
        }

        return result
    }

    private fun addPlayer(username: String, accessCode: String): Task<Void> {
        val gameRef = db.collection("games").document(accessCode)
        return gameRef.update("playerList", FieldValue.arrayUnion(username))
    }

    /**
     * removes user name from games player list
     * removes listener to firebase to update game
     */
    override fun leaveGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun endGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resetGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeName() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>> {
        val result = MutableLiveData<Resource<List<List<String>>, PackDetailsError>>()

        if(!Connectivity.isOnline) {
            result.value = Resource.Error(error = PackDetailsError.NETWORK_ERROR)
            return result
        }

        val list = mutableListOf<List<String>>()

        db.collection(Collections.packs).get()
            .addOnSuccessListener { collection ->
                collection.documents.forEach { document ->
                    val pack = listOf(document.id) + document.data!!.keys.toList()
                    list.add(pack)
                }
                result.value = Resource.Success(list)

            }.addOnFailureListener {
                result.value = Resource.Error(error = PackDetailsError.UNKNOWN_ERROR)
            }

        return result
    }

    private suspend fun generateAccessCode(): String {
        var newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        while(db.collection(Collections.games).document(newCode).get().await().exists()) {
            newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        }
        return newCode
    }
}