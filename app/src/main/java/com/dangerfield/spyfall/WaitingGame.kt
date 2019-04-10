package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_waiting_game.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class WaitingGame : AppCompatActivity() {

    var playerList = ArrayList<Player>()
    val ACCESS_CODE = generateAccessCode()
    var db = FirebaseFirestore.getInstance()
    var locations = ArrayList<String>()
    lateinit var gameLocation: String
    lateinit var roles: ArrayList<String>
    private var timeLimit: Int = 0  //THIS IS NOT BEST PRACTICE
    private var checkedBoxes = mutableListOf<String>()
    private var TAG = "Waiting Game"

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_game)



        timeLimit = intent.getIntExtra("TIME_LIMIT",0)
        checkedBoxes = intent.getStringArrayListExtra("CHECKED_BOXES").toMutableList()
        Log.d(TAG,"Checked boxes are: $checkedBoxes")
        val playerName = intent.getStringExtra("PLAYER_NAME")

        getLocationsAndRolesFromFireBase()



        //pull data and add players
        playerList.add(Player("none","Elijah",0))
        playerList.add(Player("none","Bri",0))
        playerList.add(Player("none","Josiah",0))
        playerList.add(Player("none","Nibraas",0))
        playerList.add(Player("none","George",0))






        val adapter = PlayerAdapter(playerName,playerList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        tv_acess_code.text = ACCESS_CODE



    }

    fun onStartClick(view: View){
        val intent = Intent(this,GameActivity::class.java)
        intent.putExtra("ACCESS_CODE",ACCESS_CODE)
        intent.putExtra("LOCATIONS",locations)
        startActivity(intent)
    }


    fun generateAccessCode(): String {

        return UUID.randomUUID().toString().substringBefore("-").toUpperCase()

    }

    fun createFireBaseGame(timeLimit: Int){
        //create a node on firebase with the ACCESS_CODE variable with children of timelimit and player list
       val ref = FirebaseDatabase.getInstance().getReference("/games/$ACCESS_CODE")

        var game: Game = Game(gameLocation,checkedBoxes, timeLimit, playerList)
        ref.setValue(game)


    }

    fun getLocationsAndRolesFromFireBase() {

        Log.d(TAG,"trying to get pack: ${checkedBoxes.get(0)}")


        val collectionRef = db.collection(checkedBoxes.get(0))
        collectionRef.get().addOnSuccessListener { documents ->

            //grab all locations in  pack, this will be passed into intent
            documents.onEach { locations.add(it.id) }
            Log.d(TAG,"locations = ${locations}")

            //from this you can grab the string and all the roles
            var randomLocation = documents.toList().get(Random().nextInt(documents.size()))
            gameLocation = randomLocation.id
            roles = randomLocation.data["roles"] as ArrayList<String>
            Log.d(TAG,"roles for location ${gameLocation} is ${roles}")

            //okay so now you can assign the game chosen location as well as each players roles.
            //just shuffle the roles and assign in order
            createFireBaseGame(timeLimit)

        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


    }



}



