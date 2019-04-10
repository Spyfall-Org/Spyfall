package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun onClickNewGame(view: View){
        val intent = Intent(this,NewGameActivity::class.java)
        startActivity(intent)
    }

    fun onClickJoinGame(view: View){
        val intent = Intent(this,JoinGameActivity::class.java)
        startActivity(intent)
    }
}
