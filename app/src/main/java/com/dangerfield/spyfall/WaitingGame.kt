package com.dangerfield.spyfall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_waiting_game.*
import androidx.recyclerview.widget.DividerItemDecoration



class WaitingGame : AppCompatActivity() {

    var playerList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_game)


            //pull data and add players
        playerList.add("Elijah")
        playerList.add("Josiah")
        playerList.add("George")
        playerList.add("Bri")

        val adapter = PlayerAdapter(playerList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }


}
