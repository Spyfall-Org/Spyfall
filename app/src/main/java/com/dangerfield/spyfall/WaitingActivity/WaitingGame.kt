package com.dangerfield.spyfall.WaitingActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.GameActivity
import com.dangerfield.spyfall.MainActivity
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.data.Game
import com.dangerfield.spyfall.data.Player
import kotlinx.android.synthetic.main.activity_waiting_game.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class WaitingGame : AppCompatActivity() {

    var playerList = ArrayList<String>()
    var db = FirebaseFirestore.getInstance()
    private lateinit var database: DatabaseReference
    private lateinit var ACCESS_CODE: String
    lateinit var players: ArrayList<String>
    private var TAG = "Waiting Game"
    lateinit var playerName : String
    lateinit var currentPlayer : Player
    lateinit var adapter: PlayerAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_game)

        ACCESS_CODE = intent.getStringExtra("ACCESS_CODE")
        playerName = intent.getStringExtra("PLAYER_NAME")

        tv_acess_code.text = ACCESS_CODE



        adapter = PlayerAdapter(playerName, playerList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        displayUsers()



    }

    fun onStartClick(view: View){
       // val intent = Intent(this, GameActivity::class.java)
       // startActivity(intent)
    }

    fun onLeaveClick(view:View){
        //called when user clicks leave game
        val gameRef = db.collection("games").document("$ACCESS_CODE")

        //remove player
        gameRef.update("playerList", FieldValue.arrayRemove("$playerName"))

        //if all players left, delete the document
        gameRef.get()
            .addOnSuccessListener { game ->
                if((game["playerList"] as ArrayList<String>).isEmpty()){
                    gameRef.delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        val intent = Intent(this, MainActivity::class.java)


        startActivity(intent)

    }

    fun displayUsers(){
        val gameRef = db.collection("games").document("$ACCESS_CODE")
            gameRef.addSnapshotListener(EventListener<DocumentSnapshot>{ Game ,e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                if (Game != null && Game.exists()) {
                    Log.d(TAG, "Current game data: ${Game.data}")
                    playerList.clear()
                    playerList.addAll(Game["playerList"] as ArrayList<String>)

                    adapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Current data: null")
                }
            })

    }




    fun getLocationsAndRolesFromFireBase(checkedBoxes: ArrayList<String>) {

        Log.d(TAG,"trying to get pack: ${checkedBoxes.get(0)}")

        var db = FirebaseFirestore.getInstance()
        //TODO get all location packs and select a random 30 unless the size is 1, then just 20
        val collectionRef = db.collection(checkedBoxes.get(0))
        collectionRef.get().addOnSuccessListener { documents ->

            //grab all locations in  pack, this will be passed into intent
            var index = Random().nextInt(documents.toList().size)
            var randomLocation = documents.toList()[index]
            var gameLocation = randomLocation.id
            var roles = randomLocation.data["roles"] as ArrayList<String>
            Log.d(TAG,"roles for location ${gameLocation} is ${roles}")



        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }





//    fun loadPlayers(){
//
//        //I dont think we can assign roles upon creating. We wont know how many people there are
//        //assigning roles may need to go into the game activity where theres code like this that loads in
//        //the completed list
//
//        //i think play list will start off as a string array and then that string array will get pulled in
//        //and the node will be updated using the following code
//
//        roles.shuffle()
//        //pull data and add players
//        Log.d(TAG,"grabbed roles[${playerList.size}]")
//        currentPlayer = Player(roles.get(playerList.size), playerName, 0)
//        playerList.add(currentPlayer)
//        Log.d(TAG,"grabbed roles[${playerList.size}]")
//        playerList.add(Player(roles.get(playerList.size), "Bri", 0))
//        Log.d(TAG,"grabbed roles[${playerList.size}]")
//        playerList.add(Player(roles.get(playerList.size), "Blythe", 0))
//
//        //pick a random player and make their role "the spy"
//
//        playerList[Random().nextInt(playerList.size)].role = "the spy!"
//
//        val adapter = PlayerAdapter(playerName, playerList, this)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//
//
//    }


}



