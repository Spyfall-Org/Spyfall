package com.dangerfield.spyfall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_waiting_game.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class WaitingGame : AppCompatActivity() {

    var playerList = ArrayList<String>()
    val ACCESS_CODE = generateAccessCode()

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_game)

        val timeLimit = intent.getIntExtra("TIME_LIMIT",0)
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


    fun generateAccessCode(): String {

        return UUID.randomUUID().toString().substringBefore("-").toUpperCase()

    }

    fun createFireBaseGame(timeLimit: Int){
        //create a node on firebase with the ACCESS_CODE variable with children of timelimit and player list
        val ref = FirebaseDatabase.getInstance().getReference("/$ACCESS_CODE").push()
        ref.setValue("CREATED")
        ref.child("Players").push().setValue(playerList)
        ref.child("Time").push().setValue(timeLimit)


    }


}
