package com.dangerfield.spyfall.newGame


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.dangerfield.spyfall.CustomClasses.UIHelper

import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.joinGame.GameViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_new_game.*
import java.util.*
import kotlin.collections.ArrayList

class NewGameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        tv_new_game_name.onFocusChangeListener = UIHelper.keyboardHider
        tv_new_game_time.onFocusChangeListener = UIHelper.keyboardHider

        btn_create.setOnClickListener { createGame(it) }
    }

    fun createGame(sender: View){

        var timeLimit = tv_new_game_time.text.toString().trim()
        var playerName = tv_new_game_name.text.toString().trim()
        val chosenPacks = ArrayList<String>()

        //these strings will be used for queries of the firestore database for which locations to include
        if(cb_pack1.isChecked) chosenPacks.add("pack 1")
        if(cb_pack2.isChecked) chosenPacks.add("pack 2")
        if(cb_special_pack.isChecked) chosenPacks.add("special pack")

        when {
            chosenPacks.isEmpty() -> {Toast.makeText(context,"Please select a pack", Toast.LENGTH_LONG).show()
                return}

            playerName.isEmpty() -> {Toast.makeText(context, "please enter a name", Toast.LENGTH_LONG).show()
                return}

            timeLimit.isEmpty() || timeLimit.toInt() > 10 -> {
                Toast.makeText(context, "please enter a time limit less than 10 minutes", Toast.LENGTH_LONG).show()
                return
            }
        }

        val playerList = mutableListOf<String>(playerName)
        //push timeLimit, player name as an array, isStarted as false, and included packs
        createFireBaseGame(timeLimit.toInt(), playerList as ArrayList<String>,false,chosenPacks)

        Navigation.findNavController(sender).navigate(R.id.action_newGameFragment_to_waitingFragment)

    }

    //Start the game
    fun createFireBaseGame(timeLimit: Int,playerList: ArrayList<String>,isStarted: Boolean, chosenPacks: ArrayList<String>){


        val db = viewModel.db
        val ACCESS_CODE = viewModel.ACCESS_CODE

        val game = HashMap<String, Any>()
        game["timeLimit"] = timeLimit
        game["playerList"] = playerList
        game["isStarted"] = isStarted
        game["chosenPacks"] = chosenPacks

        //create a node on firebase with the ACCESS_CODE variable with children of timelimit and player list

        db.collection("games").document("$ACCESS_CODE")
            .set(game)
            .addOnSuccessListener { Log.d("New Game", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("New Game", "Error writing document", e) }
    }


}
