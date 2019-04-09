package com.dangerfield.spyfall

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

import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.collections.HashMap
import com.google.firebase.firestore.FirebaseFirestore




class GameActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    lateinit var ACCESS_CODE: String
    val TAG = "Game Activity"
    var game: Game? = null
    var locations = ArrayList<String>()

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

                loadPlayers(game?.playerList!!)
                startTimer(game?.timeLimit!!)
                getLocationsFromFireBase()
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

    fun loadPlayers(playerList: ArrayList<Player>){

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




    fun getLocationsFromFireBase(){
        //this method should load in all of the locations that the user chose
        //option: game has a node of chosen packs and chosen location

       Log.d(TAG,"trying to get pack: ${game?.locationPacks?.get(0)}")

        val collectionRef = db.collection("${game?.locationPacks?.get(0)}")
        collectionRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("including location", document.id)
                locations.add(document.id)
            }
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


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

}
