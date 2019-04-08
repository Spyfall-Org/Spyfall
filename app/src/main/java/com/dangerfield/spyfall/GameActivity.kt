package com.dangerfield.spyfall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.setTag
import android.R
import android.view.View


class GameActivity : AppCompatActivity() {

    lateinit var ACCESS_CODE: String
    val TAG = "Game Activity"
    var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        ACCESS_CODE = intent.getStringExtra("ACCESS_CODE")
        getGameFromFireBase()


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

    fun loadPlayers(playerList: ArrayList<String>){

        //for every 2 players, create a row

        var row1 = TableRow(this)
        row1.apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
        }

        var player_tv = TextView(this)

       // var inflate = View.inflate(this, R.layout.player_card, null) as TableRow
        //set tag for each TableRow
        //inflate.setTag(0)
        //player_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
//        player_tv.apply {
//            text = playerList[0]
//            background = resources.getDrawable(R.drawable.background_text)
//            height = 75
//            width = 200
//            gravity = Gravity.CENTER
//        }



        row1.addView(player_tv)


        tbl_players.addView(row1)


    }

}
