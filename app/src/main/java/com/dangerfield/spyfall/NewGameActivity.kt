package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class NewGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
    }


    fun onCreateClick(view : View){
        val intent = Intent(this,WaitingGame::class.java)
        startActivity(intent)
    }
}
