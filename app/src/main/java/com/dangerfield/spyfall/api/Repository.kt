package com.dangerfield.spyfall.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.joinGame.JoinGameError
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.util.Connectivity
import com.dangerfield.spyfall.util.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Job
import kotlinx.coroutines.tasks.await
import java.util.*

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
    override fun createGame(chosenPacks: List<String>, timeLimit: Int, username: String): LiveData<Event<String>> {
        val result = MutableLiveData<Event<String>>()

        //TODO get access code and locations list async and await on both of them
        /*
        generate access code
        get list of all locations from chosen packs
        create game object and update ref
        set result even with access code
         */
        return result
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

                        username.length > 25 ->
                            result.value = Resource.Error(error = JoinGameError.NAME_CHARACTER_LIMIT)

                        else -> {
                            addPlayer(username, accessCode).addOnSuccessListener {
                                Crashlytics.log("Player: \"$username\" has joined $accessCode")
                                val gameRef: DocumentReference = db.collection("games").document(accessCode)
                                currentSession = CurrentSession(accessCode, username).build(gameRef)
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

    private suspend fun generateAccessCode(): String {
        var newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        while(db.collection(Collections.games).document(newCode).get().await().exists()) {
            newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        }
        return newCode
    }
}