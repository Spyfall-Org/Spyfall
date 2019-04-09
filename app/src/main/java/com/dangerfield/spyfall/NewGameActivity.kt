package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_game.*

class NewGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)


    }


    fun onCreateClick(view : View){

        if(tv_time.text.isEmpty() || tv_time.text.toString().toInt() > 10){
            Toast.makeText(this, "please enter a time limit less than 10 minutes",Toast.LENGTH_LONG).show()
            return
        }
        if(tv_in_game_player_name.text.isEmpty()){
            Toast.makeText(this, "please enter a name",Toast.LENGTH_LONG).show()
            return
        }
        val timeLimit = tv_time.text.toString().toInt()
        val playerName = tv_in_game_player_name.text.toString()

        val intent = Intent(this,WaitingGame::class.java)
        intent.putExtra("TIME_LIMIT", timeLimit)
        intent.putExtra("PLAYER_NAME", playerName)

        startActivity(intent)
    }


}
