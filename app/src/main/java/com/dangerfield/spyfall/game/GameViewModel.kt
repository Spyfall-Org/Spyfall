package com.dangerfield.spyfall.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Player
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.ArrayList

class GameViewModel : ViewModel() {
    // I might just end up having this be like one game object
    private var playerNames: MutableLiveData<ArrayList<String>> = MutableLiveData()
    var playerObjectList= ArrayList<Player>()
    var gameHasStarted: MutableLiveData<Boolean> = MutableLiveData()
    var location: MutableLiveData<String> = MutableLiveData()
    var roles= ArrayList<String>()
    var timeLimit: Long = 0
    var chosenPacks = ArrayList<String>()

    var gameObject: MutableLiveData<Game> = MutableLiveData()

    var db = FirebaseFirestore.getInstance()
    //start off with a uuid but if its changed, so is the reference
    var ACCESS_CODE: String = UUID.randomUUID().toString().substring(0,6).toLowerCase()
        set(value){
            field = value
            gameRef = db.collection("games").document(value)
        }
    var gameRef = db.collection("games").document(ACCESS_CODE)


    var allLocations: MutableLiveData<ArrayList<String>> = MutableLiveData()
    lateinit var currentUser: String



    //so I want playerNames to be listening to firebases playerlist
    fun getGameUpdates(): LiveData<ArrayList<String>>  {

        gameRef.addSnapshotListener { game, error ->

            if (error != null) {
                Log.w("View Model", "Listen failed.", error)
                return@addSnapshotListener
            }


            if (game != null && game.exists()) {
                gameObject.value = game?.toObject(Game::class.java)

//                timeLimit = game["timeLimit"] as Long
//                chosenPacks = game["chosenPacks"] as ArrayList<String>
//                playerObjectList = game["playerObjectList"] as ArrayList<Player>
//                location.value = game["chosenLocation"] as String
//                gameHasStarted.value = game["isStarted"] as Boolean
//                playerNames.value = game["playerList"] as ArrayList<String>
            }else {
                Log.d("View Model", "Current data: null")
            }
        }
        return playerNames
    }

    //okay so now I just want to have getting the locations and assigning players and such here for the actual game screen

    fun getRandomLocation(): LiveData<String> {

        if(location.value.isNullOrEmpty()){

            gameRef.get().addOnSuccessListener {
                chosenPacks = it.get("chosenPacks") as ArrayList<String>

                //selects one of the packs at random
                val randomPack = chosenPacks[Random().nextInt(chosenPacks.size)]

                val collectionRef = db.collection(randomPack)
                collectionRef.get().addOnSuccessListener { documents ->
                    //get a random location and add it to the game node
                    val index = Random().nextInt(documents.toList().size)
                    val randomLocation = documents.toList()[index]
                    //collects all of the roels for a random location
                    (randomLocation["roles"] as ArrayList<String>).forEach { roles.add(it) }

                    location.value = randomLocation.id

                    val location = HashMap<String,String>()
                    location["chosenLocation"] = randomLocation.id
                    gameRef.set(location, SetOptions.merge())
                }
                    .addOnFailureListener { exception ->
                        Log.w("Game view model", "Error getting documents: ", exception)
                    }
            }
        }
        return location
    }


    fun startGame() {
        //assignes all roles
        if(roles.isNullOrEmpty() or playerNames.value.isNullOrEmpty()){ return }

        playerNames.value!!.shuffle()
        roles.shuffle()

        for (i in 0 until playerNames.value!!.size - 1) {
            //we can guarentee that i will never be out of index for roles as an 8 player max is enforced
            //i is between 0-6
            playerObjectList.add(Player(roles[i], playerNames.value!![i], 0))
        }
        //so we shuffled players and roles and assigned everyone except one a role in order
        //now we assign the last one as the spy
        playerObjectList.add(Player("The Spy!", playerNames.value!!.last(), 0))

        //now push to database
        gameRef.update("playerObjectList", playerObjectList)
        gameRef.update("isStarted", true)

    }

    fun getAllLocations():  LiveData<ArrayList<String>> {
        var completedTasks = 0
        var tempLocations = ArrayList<String>()
        //if more than one pack was chosen, load all locations, shuffle, pick first 30

        for (i in 0 until chosenPacks.size) {

            Log.d("Game View Model", "chosenPack: ${chosenPacks[i]}")

            db.collection(chosenPacks[i]).get().addOnSuccessListener { location ->
                location.documents.forEach { tempLocations.add(it.id) }
                Log.d("Game View Model", "ALL LOCAITONS: ${allLocations}")

            }.addOnCompleteListener {
                completedTasks += 1
                if(completedTasks == chosenPacks.size){

                    allLocations.value = tempLocations
                }

            }
        }
        return this.allLocations
    }

    fun endGame(){
        val gameRef = db.collection("games").document(ACCESS_CODE)
        gameRef.delete()
    }

    fun removePlayer(){
        val gameRef = db.collection("games").document(ACCESS_CODE)
        //remove player
        gameRef.update("playerList", FieldValue.arrayRemove(currentUser))

    }

    fun assignRolesAndStartGame() {

        for (i in 0 until chosenPacks.size) {
            db.collection(chosenPacks[i]).whereEqualTo("location", location.value)
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


}
