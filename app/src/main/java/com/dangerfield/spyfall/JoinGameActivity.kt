package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dangerfield.spyfall.WaitingActivity.WaitingGame
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_join_game.*

class JoinGameActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    val TAG = "JoinGame"
    lateinit var ACCESS_CODE: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

    }


    fun joinGameClick(view: View) {
        //take access code and ask if that node exists
        //if it does add that player to the player list and take to waiting screen  passing "PLAYER_NAME
        //from there everything should be normal
        //if it doesnt make a toast
        if (tv_access_code.text.isEmpty()) {
            Toast.makeText(this, "Please type in an access code", Toast.LENGTH_LONG).show()
            return
        }
        if (tv_username.text.isEmpty()) {
            Toast.makeText(this, "Please type in a username", Toast.LENGTH_LONG).show()
            return
        }
        ACCESS_CODE = tv_access_code.text.toString()

        db.collection("games").document("$ACCESS_CODE")
            .update("playerList", FieldValue.arrayUnion("${tv_username.text}"))


            val intent = Intent(this, WaitingGame::class.java)
            val playerName = tv_username.text.toString()
            intent.putExtra("ACCESS_CODE",ACCESS_CODE)
            intent.putExtra("PLAYER_NAME", playerName)
            startActivity(intent)
        }


    }


