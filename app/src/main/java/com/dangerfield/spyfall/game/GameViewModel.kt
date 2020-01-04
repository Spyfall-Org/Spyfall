package com.dangerfield.spyfall.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Player
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
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

    var hasNetworkConnection: MutableLiveData<Boolean> = MutableLiveData()
    var gameExists: MutableLiveData<Boolean> = MutableLiveData()
    var roles= ArrayList<String>()
    var gameObject: MutableLiveData<Game> = MutableLiveData()
    var db = FirebaseFirestore.getInstance()
    var gameRef = db.collection("games").document(ACCESS_CODE)
    var gameLocations: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private lateinit var gameListener: ListenerRegistration

    lateinit var currentUser: String

    fun getGameUpdates(): MutableLiveData<Game> {

        // we need to make it such that when gameref changes, so does the game ref in this snap shot listener
        gameListener = gameRef.addSnapshotListener { game, error ->
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

    /*
    Selects random pack and then a random loaction from that pack
    moves the random pack to the 0th index on firebase (in onComplete)
    to make it easier to grab roles later on
     */
    fun getRandomLocation(chosenPacks: ArrayList<String>, onComplete: ((location: String, chosenPacks: ArrayList<String>) -> Unit)? = null) {
        val randomPack = chosenPacks.random()
        db.collection("packs").document(randomPack)
            .get().addOnSuccessListener {
                val location = it.data?.toList()?.random()?.first ?: ""
                //places the pack with the chosen location at index 0
                Collections.swap(chosenPacks, 0, chosenPacks.indexOf(randomPack))
                onComplete?.invoke(location, chosenPacks)
            }
    }

    fun getRolesAndStartGame() {
        if(gameObject.value?.started == true) return
        roles.clear()
        gameRef.update("started", true)

        db.collection("packs").document(gameObject.value!!.chosenPacks[0])
            .get().addOnSuccessListener {
                val mRoles = it[gameObject.value!!.chosenLocation] as List<String>
                roles.addAll(mRoles)
                startGame()
            }
    }

    fun startGame() {

        //assignes all roles
        if(roles.isNullOrEmpty() or gameObject.value?.playerList.isNullOrEmpty()){ return }

            val playerNames = gameObject.value?.playerList?.shuffled()
            val playerObjectList = ArrayList<Player>()
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

            incrementGamesPlayed()
    }

    fun getAllGameLocations():  LiveData<ArrayList<String>> {
        val tempLocations = ArrayList<String>()
        gameObject.value?.let {game ->
            db.collection("packs").get().addOnSuccessListener { collection ->
                collection.documents.forEach { document ->
                    if (game.chosenPacks.contains(document.id)) tempLocations.addAll(document.data!!.keys.toList())
            }
            }.addOnCompleteListener {
                gameLocations.value = tempLocations
            }
        }
        return gameLocations
    }
    
    fun endGame(): Task<Void> {
        gameListener.remove()
        //sets to false such that listener in game fragment can be triggered
        gameExists.value = false

        // delete the game on the server
       return gameRef.delete()
    }

    fun getNewAccessCode(onComplete: ((code: String) -> Unit)?) {
        var newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        db.collection("games").document(newCode).get().addOnCompleteListener {
            if(it.result?.exists() == true) {
                //then the document exists and thus the game code has already been taken
                getNewAccessCode(onComplete)
            }else{
                onComplete?.invoke(newCode)
            }
        }
    }


    fun resetGame()  {
        // resets variables on firebase, which will update viewmodel
        getRandomLocation(gameObject.value?.chosenPacks ?: return) { location, packs ->
            val newGame = Game(location,packs,false,
                gameObject.value!!.playerList, ArrayList(),gameObject.value!!.timeLimit)
            gameRef.set(newGame)
        }
    }

    fun createGame(game: Game, code: String, onComplete: (() -> Unit)? = null) {
        getRandomLocation(game.chosenPacks) { location , packs ->
            game.chosenLocation = location
            game.chosenPacks = packs
            gameObject.value = game
            ACCESS_CODE = code
            gameRef.set(game).addOnCompleteListener {
                onComplete?.invoke()
            }
        }
    }

    fun removePlayer(): Task<Void>{
       //when a player leaves a game, you dont want them to hold onto the game data
        gameObject = MutableLiveData()
        gameListener.remove()
        //set to false such that when a user is not timeout removed to a game they already left
        gameExists.postValue(false)
        return gameRef.update("playerList", FieldValue.arrayRemove(currentUser)).addOnSuccessListener {
            if(gameObject.value?.playerList?.size == 0){ endGame()}
        }
    }

    fun addPlayer(player: String) = gameRef.update("playerList", FieldValue.arrayUnion(player))

    fun changeName(newName: String): Task<Void> {
        val index = gameObject.value!!.playerList.indexOf(currentUser)
        gameObject.value!!.playerList[index] = newName
        currentUser = newName
        return gameRef.update("playerList", gameObject.value!!.playerList)
    }

    fun incrementGamesPlayed(){
        //this function is used to keep stats about how many Android games have been played
        db.collection("stats")
            .document("game").update("num_games_played",FieldValue.increment(1))
    }

    fun incrementAndroidPlayers(){
        //this function is used to keep stats about how many Android games have been played
        db.collection("stats")
            .document("game").update("android_num_of_players",FieldValue.increment(1))
    }
}
