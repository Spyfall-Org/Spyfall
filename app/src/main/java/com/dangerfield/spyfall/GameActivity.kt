package com.dangerfield.spyfall

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

import android.view.LayoutInflater
import android.view.View
import android.widget.TableLayout

import androidx.constraintlayout.widget.ConstraintLayout
import com.dangerfield.spyfall.data.Game
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.simple_card.*


class GameActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    lateinit var ACCESS_CODE: String
    val TAG = "Game Activity"
    var game: Game? = null
    lateinit var playerName: String
    lateinit var chosenLocation: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        ACCESS_CODE = intent.getStringExtra("ACCESS_CODE")
        //getGameFromFireBase()
        playerName = intent.getStringExtra("PLAYER_NAME")

        getGameData()
        listenForEnd()
    }

    fun listenForEnd(){
        var gameRef = db.collection("games").document(ACCESS_CODE)
        gameRef.addSnapshotListener(EventListener<DocumentSnapshot> { game, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }


            if (game != null && game.exists()) {
               if(game["isStarted"] == false){

                   //Start Intent
                   val intent = Intent(this,MainActivity::class.java)
                   startActivity(intent)
                   val gameRef = db.collection("games").document(ACCESS_CODE)
                   gameRef.get().addOnSuccessListener { game ->
                       if(game.exists()){
                           gameRef.delete()
                       }
                   }
                   finish()
               }
            } else {
                Log.d(TAG, "Current data: null")
            }
        })
    }
    fun getGameData(){

        db.collection("games").document(ACCESS_CODE).get().addOnSuccessListener { game ->

            var gameObject =game.toObject(Game::class.java)

            if(gameObject != null) {
                var timeLimit = gameObject.timeLimit
                var playerList = gameObject.playerList
                var chosenPacks = gameObject.chosenPacks
                var currentPlayer = gameObject.playerObjectList.filter { it.username == playerName }
                if(currentPlayer.size ==1){
                    tv_role.text = currentPlayer[0].role
                    if(currentPlayer[0].role != "The Spy!"){
                        tv_chosen_location.text = "Location: ${gameObject?.chosenLocation}"
                    }else{
                        tv_chosen_location.text = "Figure out the location!"
                    }
                }
                //we might be able to load these last views on different threads
                loadLocationView(chosenPacks)
                loadViews(playerList, tbl_players)
                startTimer(timeLimit.toLong())
            }
        }

    }

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

    fun loadLocationView(chosenPacks: ArrayList<String>){

        var locations = ArrayList<String>()
        var completedTasks = 0
        //if more than one pack was chosen, load all locations, shuffle, pick first 30

        for(i in 0 until chosenPacks.size) {

            Log.d(TAG,"chosenPack: ${chosenPacks[i]}")

            var gameRef = db.collection(chosenPacks[i]).get().addOnSuccessListener { location ->
                location.documents.forEach { locations.add(it.id) }
                Log.d(TAG,"ALL LOCAITONS: ${locations}")
                //this makes sure all of the code has completed


            }.addOnCompleteListener {
                    completedTasks+=1
                Log.d(TAG,"completed tasks: ${completedTasks}")
                if(completedTasks == chosenPacks.size){
                    if(chosenPacks.size > 1) {
                        //TODO once every pack has 20 you can uncommment this code, right now it loads all documents
                        //grab the first 30, and shuffle them
                        //loadViews(locations.subList(0, 29).shuffle() as ArrayList<String>, tbl_locations)
                        //WHEN GRABBING SUBLIST MAKE SURE IT INCLUDES THE CHOSEN LOCATION
                        locations.shuffle()
                        loadViews(locations, tbl_locations)

                    }else{
                        //we only grabbed one pack so just load those
                        loadViews(locations, tbl_locations)
                    }
                }


            }
        }





    }

    fun loadViews(list: ArrayList<String>, table: TableLayout){

        //TODO considering we will have 20 or 30 every time we can probably avoid this
        var params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        params.setMargins(10,10,10,10)


        for( i in 0 until list.size step 2) {
            val row = TableRow(this).apply {
                layoutParams = params
            }
            for(j in 0..1) {
                val player_tv = LayoutInflater.from(this)
                    .inflate(R.layout.simple_card, row, false) as ConstraintLayout
                player_tv.setOnClickListener {
                    onClickStrikeThrough(it as ConstraintLayout)
                }
                var tv = player_tv.getViewById(R.id.tv_simple_card) as TextView
                if(i+j < list.size){
                    tv.text = list[i + j]
                    row.addView(player_tv)
                }

            }
            table.addView(row)
        }

    }
    fun endGame(view: View){
        //called when end button game is clicked
        //a listener is set checking for isStarted = false and if it does it goes back to the home screen
        //and calls finish()
        var gameRef = db.collection("games").document(ACCESS_CODE)



    }



    fun onClickStrikeThrough(layout: ConstraintLayout){
        //TODO change this to cross through
        var view = layout.getViewById(R.id.tv_simple_card) as TextView
        if(view.paintFlags != Paint.STRIKE_THRU_TEXT_FLAG){
            view.setTextColor(resources.getColor(R.color.colorLightGrey))
            view.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        }else{
            view.setTextColor(resources.getColor(R.color.colorPrimary))
            view.paintFlags = 0
        }
    }
}
