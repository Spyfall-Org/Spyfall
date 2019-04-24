package com.dangerfield.spyfall

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
     var gameFound = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        val keyboardHider =  View.OnFocusChangeListener {view,b ->
            if(!b){
                hideKeyboard(view)
            }
        }

        tv_access_code.onFocusChangeListener = keyboardHider
        tv_username.onFocusChangeListener = keyboardHider

    }

    fun joinGameClick(view: View) {

        if (tv_access_code.text.isEmpty()) {
            Toast.makeText(this, "Please type in an access code", Toast.LENGTH_LONG).show()
            return
        }
        if (tv_username.text.isEmpty()) {
            Toast.makeText(this, "Please type in a username", Toast.LENGTH_LONG).show()
            return
        }
        ACCESS_CODE = tv_access_code.text.toString().trim()

        joinIfGameExists(ACCESS_CODE)

    }


    fun joinIfGameExists(access_code: String){
        db.collection("games").document(access_code).get().addOnSuccessListener { game ->
            if(game.exists()){
                var list = (game["playerList"] as ArrayList<String>)

                   when {
                       list.size >= 8 ->  Toast.makeText(this, "Sorry, the max for a game is currently 8 players", Toast.LENGTH_LONG).show()
                       game["isStarted"]==true -> Toast.makeText(this, "Sorry, this game has been started", Toast.LENGTH_LONG).show()
                       list.contains(tv_username.text.toString().trim()) -> Toast.makeText(this, "Sorry, that name is taken by another player", Toast.LENGTH_LONG).show()
                       else -> joinGame()
                   }

            }else{
                Toast.makeText(this, "Sorry, no game was found with that access code", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun joinGame() {
        db.collection("games").document(ACCESS_CODE)
            .update("playerList", FieldValue.arrayUnion("${tv_username.text}"))

        val intent = Intent(this, WaitingGame::class.java)
        val playerName = tv_username.text.toString().trim()
        intent.putExtra("FROM_ACTIVITY", "JOIN_GAME_ACTIVITY")
        intent.putExtra("ACCESS_CODE", ACCESS_CODE)
        intent.putExtra("PLAYER_NAME", playerName)
        startActivity(intent)
        finish()
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}


