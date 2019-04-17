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
import com.dangerfield.spyfall.data.Game
import com.dangerfield.spyfall.data.Player
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.HashMap


class GameActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    lateinit var ACCESS_CODE: String
    val TAG = "Game Activity"
    var game: Game? = null
    lateinit var playerName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        ACCESS_CODE = intent.getStringExtra("ACCESS_CODE")
        //getGameFromFireBase()
        playerName = intent.getStringExtra("PLAYER_NAME")

        getCurrentPlayer()

    }

    fun getCurrentPlayer(){

        db.collection("games").document(ACCESS_CODE).get().addOnSuccessListener { game ->

            var timeLimit = game["timeLimit"] as Long
            startTimer(timeLimit)


        }

    }

//    fun getGameFromFireBase(){
//        val ref = FirebaseDatabase.getInstance().getReference("/games/$ACCESS_CODE")
//
//        //this is called initially and then every time the data is changed
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                game = dataSnapshot.getValue(Game::class.java)
//
//                if(game!= null){
//
//                   // THIS IS WHERE WE COULD DO SOME ASYNC
//                loadPlayers(game?.playerList!!)
//                startTimer(game?.timeLimit!!)
////                    if(currentUser.role != "the spy!"){
////                        tv_chosen_location.text = "Location: ${game?.chosenLocation}"
////                    }else{
////                        tv_chosen_location.text = "Figure out the location!"
//
//                    }
//                }
//            }

//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//
//        })
//
//
//    }

    fun startTimer(timeLimit : Long){

        object : CountDownTimer((60000*timeLimit), 1000) {

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
                    .inflate(R.layout.simple_card, row, false) as ConstraintLayout
                var tv = player_tv.getViewById(R.id.tv_in_game_player_name) as TextView
                if(i+j < playerList.size){
                    tv.text = playerList[i + j].username
                    row.addView(player_tv)
                }

            }
            tbl_players.addView(row)
        }

    }

    fun loadLocationView(locations: ArrayList<String>){
        //TODO considering we will have 20 or 30 every time we can probably avoid this
        var params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        params.setMargins(10,10,10,10)

        for( i in 0 until locations.size step 2) {
            val row = TableRow(this).apply {
                layoutParams = params
            }
            for(j in 0..1) {
                val player_tv = LayoutInflater.from(this)
                    .inflate(R.layout.simple_card, row, false) as ConstraintLayout
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
