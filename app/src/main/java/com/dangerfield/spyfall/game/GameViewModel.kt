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
            val randomPack = gameObject.value!!.chosenPacks.random()
            db.collection("packs").document(randomPack)
                .get().addOnSuccessListener {
                    val (location,mRoles) = it.data?.toList()?.random() as Pair<String, List<String>>
                    roles.addAll(mRoles)
                    gameRef.update("chosenLocation",location)
                    //places the pack with the chosen location at index 0
                    Collections.swap(gameObject.value!!.chosenPacks,
                        0,gameObject.value!!.chosenPacks.indexOf(randomPack))
                    gameRef.update("chosenPacks", gameObject.value!!.chosenPacks)

                }
        }
    }

    fun getRolesAndStartGame() {
        //TODO: findout how to flag started to disable creat clicks here

        db.collection("packs").document(gameObject.value!!.chosenPacks[0])
            .get().addOnSuccessListener {
                val (_,mRoles) = it.data?.toList()?.random() as Pair<String, List<String>>
                roles.addAll(mRoles)
                startGame()
            }
    }

    fun startGame() {
        //if it hasnt been started then start it, this flag disbales the create button for everyone
        if(!gameObject.value!!.started) gameRef.update("started", true) else return

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

    fun removePlayer(): Task<Void>{
       //when a player leaves a game, you dont want them to hold onto the game data
        gameObject = MutableLiveData()
        roles.clear()
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
}
