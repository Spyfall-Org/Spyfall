package com.dangerfield.spyfall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class JoinGameActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)
    }


    fun onJoinGameClick(view: View){
        //take access code and ask if that node exists
        //if it does add that player to the player list and take to waiting screen  passing "PLAYER_NAME
        //from there everything should be normal
        //if it doesnt make a toast
    }
}
