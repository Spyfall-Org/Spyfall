package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

import android.view.LayoutInflater
import android.view.View

import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.collections.HashMap
import com.google.firebase.firestore.FirebaseFirestore




class GameActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    lateinit var ACCESS_CODE: String
    val TAG = "Game Activity"
    var game: Game? = null
    var locations = ArrayList<String>()
    var roles = ArrayList<String>()
    //so if we dont want duplicate roles then after we assign one we would want to make it such
    //that we cant assign that same one again.

    //IDEA: create a list of the roles and shuffle them and assign them in orger to the players



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        ACCESS_CODE = intent.getStringExtra("ACCESS_CODE")
        getGameFromFireBase()
       // getLocationsFromFireBase()


    }

    fun getGameFromFireBase(){
        val ref = FirebaseDatabase.getInstance().getReference("/games/$ACCESS_CODE")

        //this is called initially and then every time the data is changed
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                game = dataSnapshot.getValue(Game::class.java)

                if(game!= null){

                   // THIS IS WHERE WE COULD DO SOME ASYNC
                loadPlayers(game?.playerList!!)
                startTimer(game?.timeLimit!!)
                getLocationsAndRolesFromFireBase()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })


    }

    fun startTimer(timeLimit : Int){
        Log.d(TAG, "Game timer is: ${game?.timeLimit}")

        object : CountDownTimer((60000*timeLimit).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                )
                tv_timer.text = text
            }

            override fun onFinish() {
                tv_timer.text = "done!"
            }

        }.start()
    }






    fun getLocationsAndRolesFromFireBase(){
        //TODO find out why this takes so long, may have something to do with async
        //TODO think about putting this in the waiting activity and only passing the locations array

       Log.d(TAG,"trying to get pack: ${game?.locationPacks?.get(0)}")


        val collectionRef = db.collection("${game?.locationPacks?.get(0)}")
        collectionRef.get().addOnSuccessListener { documents ->

            //pick a random locatoin and then store all of that locations roles in an array
            //note: there is also a "shaffled() function

            //grab all locations in  pack
            documents.onEach { locations.add(it.id) }

            //from this you can grab the string and all the roles
            var locationSnapshot = documents.toList().get(Random().nextInt(documents.size()))
            var gameLocation = locationSnapshot.id
            var roles = locationSnapshot.data["roles"]
            Log.d(TAG,"roles for location ${gameLocation} is ${roles}")

            //okay so now you can assign the game chosen location as well as each players roles.
            //just shuffle the roles and assign in order




            //tv_role.text = set this
            loadLocationView()
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


    }

    fun loadPlayers(playerList: ArrayList<Player>){

        //TODO right now the two main views load one after the other, can we do this asynchonously?

        var params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        params.setMargins(10,10,10,10)

        for( i in 0 until playerList.size step 2) {
            val row = TableRow(this).apply {
                layoutParams = params
            }
            for(j in 0..1) {
                val player_tv = LayoutInflater.from(this)
                    .inflate(R.layout.game_player, row, false) as ConstraintLayout
                var tv = player_tv.getViewById(R.id.tv_in_game_player_name) as TextView
                if(i+j < playerList.size){
                    tv.text = playerList[i + j].username
                    row.addView(player_tv)
                }

            }
            tbl_players.addView(row)
        }

    }

    fun loadLocationView(){
        var params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        params.setMargins(10,10,10,10)

        for( i in 0 until locations.size step 2) {
            val row = TableRow(this).apply {
                layoutParams = params
            }
            for(j in 0..1) {
                val player_tv = LayoutInflater.from(this)
                    .inflate(R.layout.game_player, row, false) as ConstraintLayout
                var tv = player_tv.getViewById(R.id.tv_in_game_player_name) as TextView
                if(i+j < locations.size){
                    tv.text = locations[i + j]
                    row.addView(player_tv)
                }

            }
            tbl_locations.addView(row)
        }

    }

    fun endGame(view: View){
        //called when end button game is clicked
        //deleted node on firebase

        val ref = FirebaseDatabase.getInstance().getReference("/games/$ACCESS_CODE")
        ref.removeValue()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()


    }



}
