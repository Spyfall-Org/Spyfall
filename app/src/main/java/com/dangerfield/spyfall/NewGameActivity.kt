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


class NewGameActivity : AppCompatActivity() {

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

        if(tv_time.text.isEmpty() || tv_time.text.toString().toInt() > 10){
            Toast.makeText(this, "please enter a time limit less than 10 minutes",Toast.LENGTH_LONG).show()
            return
        }
        if(tv_in_game_player_name.text.isEmpty()){
            Toast.makeText(this, "please enter a name",Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this, WaitingGame::class.java)
        val timeLimit = tv_time.text.toString().toInt()
        val playerName = tv_in_game_player_name.text.toString()

        intent.putExtra("TIME_LIMIT", timeLimit)
        intent.putExtra("PLAYER_NAME", playerName)

        val checkedBoxes = ArrayList<String>()
        if(cb_pack1.isChecked) checkedBoxes.add("pack 1")
        if(cb_pack2.isChecked) checkedBoxes.add("pack 2")
        if(cb_pack3.isChecked) checkedBoxes.add("pack 3")

        if(checkedBoxes.isEmpty()) {
            Toast.makeText(this,"Please select a pack",Toast.LENGTH_LONG).show()
        return}

        Log.d("NEWGAME","Checked Boxes in new game are: $checkedBoxes")

        intent.putExtra("CHECKED_BOXES",checkedBoxes)


        startActivity(intent)
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
