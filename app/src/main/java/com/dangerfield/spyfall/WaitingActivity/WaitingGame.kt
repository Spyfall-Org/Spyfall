package com.dangerfield.spyfall.WaitingActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.GameActivity
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.data.Game
import com.dangerfield.spyfall.data.Player
import kotlinx.android.synthetic.main.activity_waiting_game.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class WaitingGame : AppCompatActivity() {

    var playerList = ArrayList<Player>()
    var db = FirebaseFirestore.getInstance()
    var locations = ArrayList<String>()
    lateinit var gameLocation: String
    lateinit var roles: ArrayList<String>
    private var timeLimit: Int = 0  //THIS IS NOT BEST PRACTICE
    private var checkedBoxes = mutableListOf<String>()
    private var TAG = "Waiting Game"
    lateinit var playerName : String
    lateinit var currentPlayer : Player




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_game)





        getLocationsAndRolesFromFireBase()

    }

    fun onStartClick(view: View){
       // val intent = Intent(this, GameActivity::class.java)
       // startActivity(intent)
    }




    fun getLocationsAndRolesFromFireBase() {

        Log.d(TAG,"trying to get pack: ${checkedBoxes.get(0)}")

        //TODO get all location packs and select a random 30 unless the size is 1, then just 20
        val collectionRef = db.collection(checkedBoxes.get(0))
        collectionRef.get().addOnSuccessListener { documents ->

            //grab all locations in  pack, this will be passed into intent
            documents.onEach { locations.add(it.id) }
            Log.d(TAG,"locations = ${locations}")
            var index = Random().nextInt(documents.toList().size)
            var randomLocation = documents.toList()[index]
            gameLocation = randomLocation.id
            roles = randomLocation.data["roles"] as ArrayList<String>
            Log.d(TAG,"roles for location ${gameLocation} is ${roles}")

            //this code gets called after all location and players info is loaded
            loadPlayers()


        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }


    fun loadPlayers(){

        //I dont think we can assign roles upon creating. We wont know how many people there are
        //assigning roles may need to go into the game activity where theres code like this that loads in
        //the completed list

        //i think play list will start off as a string array and then that string array will get pulled in
        //and the node will be updated using the following code

        roles.shuffle()
        //pull data and add players
        Log.d(TAG,"grabbed roles[${playerList.size}]")
        currentPlayer = Player(roles.get(playerList.size), playerName, 0)
        playerList.add(currentPlayer)
        Log.d(TAG,"grabbed roles[${playerList.size}]")
        playerList.add(Player(roles.get(playerList.size), "Bri", 0))
        Log.d(TAG,"grabbed roles[${playerList.size}]")
        playerList.add(Player(roles.get(playerList.size), "Blythe", 0))

        //pick a random player and make their role "the spy"

        playerList[Random().nextInt(playerList.size)].role = "the spy!"

        val adapter = PlayerAdapter(playerName, playerList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }


}



