package com.dangerfield.spyfall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dangerfield.spyfall.WaitingActivity.WaitingGame
import kotlinx.android.synthetic.main.activity_new_game.*
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.dangerfield.spyfall.data.Game
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import java.util.*

/* collect user entered data: Current user name, included packs, time limit
-----When “create” is clicked------
-generate access code and create node on firebase with:
	Player list(String) with current user name
	isStarted (bool)
	Time limit
	Included packs
-pass access code and current user name to the waiting screen
*/



class NewGameActivity : AppCompatActivity() {

    val ACCESS_CODE = generateAccessCode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        val keyboardHider =  View.OnFocusChangeListener {view,b ->
            if(!b){
                hideKeyboard(view)
            }
        }

        tv_in_game_player_name.onFocusChangeListener = keyboardHider
        tv_time.onFocusChangeListener = keyboardHider



    }

    fun onCreateClick(view : View){

        val checkedBoxes = ArrayList<String>()
        if(cb_pack1.isChecked) checkedBoxes.add("pack 1")
        if(cb_pack2.isChecked) checkedBoxes.add("pack 2")
        if(cb_pack3.isChecked) checkedBoxes.add("pack 3")

        if(checkedBoxes.isEmpty()) {
            Toast.makeText(this,"Please select a pack",Toast.LENGTH_LONG).show()
            return}

        if(tv_time.text.isEmpty() || tv_time.text.toString().toInt() > 10){
            Toast.makeText(this, "please enter a time limit less than 10 minutes",Toast.LENGTH_LONG).show()
            return
        }
        if(tv_in_game_player_name.text.isEmpty()){
            Toast.makeText(this, "please enter a name",Toast.LENGTH_LONG).show()
            return
        }


        Log.d("NEWGAME","Checked Boxes in new game are: $checkedBoxes")
        val timeLimit = tv_time.text.toString().toInt()
        val playerName = tv_in_game_player_name.text.toString()

        //push timeLimit, player name as an array, isStarted as false, and included packs

        val intent = Intent(this, WaitingGame::class.java)
        intent.putExtra("PLAYER_NAME", playerName)
        intent.putExtra("ACCESS_CODE", ACCESS_CODE)
        startActivity(intent)
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun generateAccessCode() = UUID.randomUUID().toString().substringBefore("-").toUpperCase()

    fun createFireBaseGame(timeLimit: Int){
        //create a node on firebase with the ACCESS_CODE variable with children of timelimit and player list
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
        val ref = FirebaseDatabase.getInstance().getReference("/games/$ACCESS_CODE")

        ref.child("timeLimit").setValue(timeLimit)
        ref.child("timeLimit").setValue(timeLimit)


    }



}
