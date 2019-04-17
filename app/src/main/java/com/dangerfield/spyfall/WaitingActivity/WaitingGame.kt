package com.dangerfield.spyfall.WaitingActivity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.GameActivity
import com.dangerfield.spyfall.MainActivity
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.data.Player
import kotlinx.android.synthetic.main.activity_waiting_game.*
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class WaitingGame : AppCompatActivity() {

    var playerList = ArrayList<String>()
    var playerObjectList = ArrayList<Player>()
    var db = FirebaseFirestore.getInstance()
    private lateinit var database: DatabaseReference
    private lateinit var ACCESS_CODE: String
    lateinit var players: ArrayList<String>
    private var TAG = "Waiting Game"
    lateinit var playerName : String
    lateinit var currentPlayer : Player
    lateinit var adapter: PlayerAdapter
    var roles = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_game)

        ACCESS_CODE = intent.getStringExtra("ACCESS_CODE")
        playerName = intent.getStringExtra("PLAYER_NAME")
        var fromActivity = intent.getStringExtra("FROM_ACTIVITY")


        tv_acess_code.text = ACCESS_CODE

        adapter = PlayerAdapter(playerName, playerList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        displayUsers()
        if(fromActivity.equals("NEW_GAME_ACTIVITY"))
        {
        getLocation()
        }

    }


    fun onStartClick(view: View){
        loadPlayerObjects() // load uses roles so we cant just do that because only the creator  has roles

        val gameRef = db.collection("games").document("$ACCESS_CODE")
        gameRef.update("isStarted", true)

        //the above line should trigger the intent put in the displayUsers() listener

    }

    fun onLeaveClick(view:View){
        //called when user clicks leave game
        val gameRef = db.collection("games").document("$ACCESS_CODE")

        //remove player
        gameRef.update("playerList", FieldValue.arrayRemove("$playerName"))

        //if all players left, delete the document
        gameRef.get()
            .addOnSuccessListener { game ->
                if((game["playerList"] as ArrayList<String>).isEmpty()){ gameRef.delete() }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    fun displayUsers(){
        val gameRef = db.collection("games").document(ACCESS_CODE)
            gameRef.addSnapshotListener(EventListener<DocumentSnapshot>{ Game ,e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                if (Game != null && Game.exists()) {
                    if(Game["isStarted"]== true){
                        val intent = WaitingGame.newIntent(this,ACCESS_CODE,playerName)
                        startActivity(intent)
                    }
                    Log.d(TAG, "Current game data: ${Game.data}")
                    playerList.clear()
                    Log.d(TAG,"Game[playerList] = ${Game["playerList"]}")
                    playerList.addAll(Game["playerList"] as ArrayList<String>)

                    adapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Current data: null")
                }
            })

    }

    fun getLocation() {

        val gameRef = db.collection("games").document("$ACCESS_CODE")

        gameRef.get().addOnSuccessListener {
            var chosenPacks = it.get("chosenPacks") as ArrayList<String>


            //TODO get all location packs and select a random 30 unless the size is 1, then just 20
            val collectionRef = db.collection(chosenPacks[0])
            collectionRef.get().addOnSuccessListener { documents ->

                //grab all locations in  pack, this will be passed into intent
                var index = Random().nextInt(documents.toList().size)
                var randomLocation = documents.toList()[index]

                var location = HashMap<String,String>()
                location["chosenLocation"] = randomLocation.id
                gameRef.set(location, SetOptions.merge())

            }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }

    }


    fun loadPlayerObjects(){
        val gameRef = db.collection("games").document(ACCESS_CODE)

        gameRef.get().addOnSuccessListener {
            var chosenPacks = it.get("chosenPacks") as ArrayList<String>
            var chosenLocation = it.get("chosenLocation") as String

            //TODO get all location packs and select a random 30 unless the size is 1, then just 20
            val rolesRef = db.collection(chosenPacks[0])
                .document(chosenLocation).get().addOnSuccessListener {locationInfo->
                    roles.clear()
                    roles = locationInfo.data?.get("roles") as ArrayList<String>

                    //so now we have all the roles
                    //get playerlist, create an object for each playerlist and assign a random role

                    roles.shuffle()
                    playerList.shuffle()

                    for(i in 0 until playerList.size-1){
                        //we can guarentee that i will never be out of index for roles as an 8 player max is enforced
                        //i is between 0-6

                        playerObjectList.add(Player(roles[i], playerList[i], 0))

                    }
                    //so we shuffled players and roles and assigned everyone except one a role in order
                    //now we assign the lst one as the spy

                    playerObjectList.add(Player("The Spy!", playerList.last(), 0))



                    //now push to database
                    var playerObjects = HashMap<String,Any?>()
                    playerObjects["playerObjectList"] = playerObjectList
                    gameRef.set(playerObjects, SetOptions.merge())

                }

                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting roles: ", exception)
                }
        }

    }

    companion object {

        fun newIntent(context: Context,ACCESS_CODE: String, playerName: String): Intent {
        val intent = Intent(context, GameActivity::class.java)
        intent.putExtra("ACCESS_CODE", ACCESS_CODE)
        intent.putExtra("PLAYER_NAME", playerName)
            return intent
        }

    }


}



