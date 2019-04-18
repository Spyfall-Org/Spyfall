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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class NewGameActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

    val TAG = "New Game"
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
        val playerName = tv_in_game_player_name.text.toString().trim()
        val playerList = mutableListOf<String>(playerName)

        //push timeLimit, player name as an array, isStarted as false, and included packs
        createFireBaseGame(timeLimit, playerList as ArrayList<String>,false,checkedBoxes)

        val intent = Intent(this, WaitingGame::class.java)
        intent.putExtra("PLAYER_NAME", playerName)
        intent.putExtra("FROM_ACTIVITY","NEW_GAME_ACTIVITY")
        intent.putExtra("ACCESS_CODE", ACCESS_CODE)
        startActivity(intent)

    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun generateAccessCode() = UUID.randomUUID().toString().substring(0,6).toLowerCase()


//TODO: we coudl possibly get the chosenLocation and roles here and never have to store the chosenPacks on firebase
    fun createFireBaseGame(timeLimit: Int,playerList: ArrayList<String>,isStarted: Boolean, chosenPacks: ArrayList<String>){

    val game = HashMap<String, Any>()
        game["timeLimit"] = timeLimit
        game["playerList"] = playerList
        game["isStarted"] = isStarted
        game["chosenPacks"] = chosenPacks

    //create a node on firebase with the ACCESS_CODE variable with children of timelimit and player list

        db.collection("games").document("$ACCESS_CODE")
            .set(game)
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

}
