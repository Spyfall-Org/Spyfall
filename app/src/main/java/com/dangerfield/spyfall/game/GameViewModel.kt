package com.dangerfield.spyfall.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.dangerfield.spyfall.customClasses.UIHelper
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Player
import com.google.android.gms.common.api.TransformedResult
import com.google.android.gms.common.api.internal.TaskUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class GameViewModel : ViewModel() {

    //start off with a uuid but if its changed, so is the reference
    var ACCESS_CODE: String = UUID.randomUUID().toString().substring(0,6).toLowerCase()
        set(value){
            field = value
            gameRef = db.collection("games").document(value)
            //the idea here is that when we have a new access code, we change the get game upadtes gameRef
            getGameUpdates()

        }

    var hasNetworkConnection: Boolean = false
    var gameExists: MutableLiveData<Boolean> = MutableLiveData()
    var roles= ArrayList<String>()
    var gameObject: MutableLiveData<Game> = MutableLiveData()
    var db = FirebaseFirestore.getInstance()
    var gameRef = db.collection("games").document(ACCESS_CODE)
    var allLocations: MutableLiveData<ArrayList<String>> = MutableLiveData()
    lateinit var currentUser: String

    fun getGameUpdates(): MutableLiveData<Game> {

        // we need to make it such that when gameref changes, so does the game ref in this snap shot listener
        gameRef.addSnapshotListener { game, error ->

            if (error != null) {
                Log.w("View Model", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (game != null && game.exists()) {
                gameObject.value = game.toObject(Game::class.java)
                gameExists.value = true
                Log.d("View Model", "game =  NOT null for gameRef ${gameRef.path}")

            }else {

                Log.d("View Model", "game =  null for gameRef ${gameRef.path}")
                gameExists.value = false
            }
        }

        return gameObject
    }


    fun getRandomLocation() {

        if (gameObject.value?.chosenLocation.isNullOrEmpty()) {

            gameRef.get().addOnSuccessListener {

                //selects one of the packs at random
                //we can garuntee that chosen packs will be here as it is sent over in the creation screen
                val randomPack = gameObject.value!!.chosenPacks.random()

                val collectionRef = db.collection(randomPack)
                collectionRef.get().addOnSuccessListener { documents ->
                    //get a random location and add it to the game node
                    val index = Random().nextInt(documents.toList().size)
                    val randomLocation = documents.toList()[index]
                    //collects all of the roels for a random location
                    (randomLocation["roles"] as ArrayList<String>).forEach { roles.add(it) }

                    gameRef.update("chosenLocation", randomLocation.id)
                }
                    .addOnFailureListener { exception ->
                        Log.w("Game view model", "Error getting documents: ", exception)
                    }
            }


        }
    }

    fun assignRolesAndStartGame() {
        //keeps other users from creating the game
        if(!gameObject.value!!.started) gameRef.update("started", true) else return

        val chosenPacks = gameObject.value!!.chosenPacks
        val location = gameObject.value!!.chosenLocation
        for (i in 0 until chosenPacks.size) {
            db.collection(chosenPacks[i]).whereEqualTo("location", location)
                .get().addOnSuccessListener { locationInfo ->

                    if (locationInfo.documents.size == 1) {
                        //only one document should be found matching the location
                        roles.addAll(locationInfo.documents[0]["roles"] as ArrayList<String>)
                        //so now we have all the roles
                        //get playerlist, create an object for each playerlist and assign a random role
                        startGame()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("events", "Error getting roles: ", exception)
                }
        }
    }


    fun startGame() {
        //if it hasnt been started then start it, this flag disbales the create button for everyone
        if(!gameObject.value!!.started) gameRef.update("started", true) else return

        //assignes all roles
        if(roles.isNullOrEmpty() or gameObject.value?.playerList.isNullOrEmpty()){ return }

            val playerNames = gameObject.value?.playerList?.shuffled()
            var playerObjectList = ArrayList<Player>()
            roles.shuffle()

            for (i in 0 until playerNames!!.size - 1) {
                //we can guarentee that i will never be out of index for roles as an 8 player max is enforced
                //i is between 0-6
                playerObjectList.add(Player(roles[i], playerNames[i], 0))
            }
            //so we shuffled players and roles and assigned everyone except one a role in order
            //now we assign the last one as the spy
            playerObjectList.add(Player("The Spy!", playerNames.last(), 0))

            //now push to database
            gameRef.update("playerObjectList", playerObjectList.shuffled()) //shuffled so that the last is not always the spy
    }

    fun getAllLocations():  LiveData<ArrayList<String>> {
        var completedTasks = 0
        var tempLocations = ArrayList<String>()
        //if more than one pack was chosen, load all locations, shuffle, pick first 30

        gameObject.value.let{game ->

            for (i in 0 until game!!.chosenPacks.size) {

                db.collection(game.chosenPacks[i]).get().addOnSuccessListener { location ->
                    location.documents.forEach { tempLocations.add(it.id) }
                    Log.d("Game View Model", "ALL LOCAITONS: ${allLocations}")

                }.addOnCompleteListener {
                    completedTasks += 1
                    if(completedTasks == game.chosenPacks.size){
                        allLocations.value = tempLocations
                    }
                }
            }
        }

        return this.allLocations
    }

    fun endGame(): Task<Void> {
        roles.clear()
        // delete the game on the server
       return gameRef.delete()
    }

    fun resetGame(): Task<Void> {
        // resets variables on firebase, which will update viewmodel
        roles.clear()

        val newGame = Game("",gameObject.value!!.chosenPacks,false,
           gameObject.value!!.playerList, ArrayList(),gameObject.value!!.timeLimit)

        return gameRef.set(newGame)

    }

    fun createGame(game: Game, code: String): Task<Void> {
            gameObject.value = game
            ACCESS_CODE = code
            return gameRef.set(game)
    }

    fun removePlayer(): Task<Void> {
       //when a player leaves a game, you dont want them to hold onto the game data
        gameObject = MutableLiveData()
        roles.clear()
        return gameRef.update("playerList", FieldValue.arrayRemove(currentUser))

    }

    fun addPlayer(player: String) = gameRef.update("playerList", FieldValue.arrayUnion(player))


    fun changeName(newName: String): Task<Void> {
        val index = gameObject.value!!.playerList.indexOf(currentUser)
        gameObject.value!!.playerList[index] = newName
        currentUser = newName
        return gameRef.update("playerList", gameObject.value!!.playerList)

    }

}
