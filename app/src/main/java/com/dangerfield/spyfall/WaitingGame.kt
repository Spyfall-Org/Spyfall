package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_waiting_game.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList


class WaitingGame : AppCompatActivity() {

    var playerList = ArrayList<String>()
    val ACCESS_CODE = generateAccessCode()
    private var timeLimit: Int = 0  //THIS IS NOT BEST PRACTICE

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_game)

        timeLimit = intent.getIntExtra("TIME_LIMIT",0)
        val playerName = intent.getStringExtra("PLAYER_NAME")

            //pull data and add players
        playerList.add("Elijah")
        playerList.add("Josiah")
        playerList.add("George")
        playerList.add("Bri")

        val adapter = PlayerAdapter(playerName,playerList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        tv_acess_code.text = ACCESS_CODE

        createFireBaseGame(timeLimit)


    }

    fun onStartClick(view: View){
        val intent = Intent(this,GameActivity::class.java)
        intent.putExtra("ACCESS_CODE",ACCESS_CODE)
        startActivity(intent)
    }


    fun generateAccessCode(): String {

        return UUID.randomUUID().toString().substringBefore("-").toUpperCase()

    }

    fun createFireBaseGame(timeLimit: Int){
        //create a node on firebase with the ACCESS_CODE variable with children of timelimit and player list
       val ref = FirebaseDatabase.getInstance().getReference("/games/$ACCESS_CODE")

        var game: Game = Game(timeLimit, playerList)
        ref.setValue(game)


    }


}



