package com.dangerfield.spyfall.newGame


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.dangerfield.spyfall.customClasses.UIHelper

import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.game.GameViewModel
import com.dangerfield.spyfall.models.Game
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //assigned in on create, as this does not need to be assigned again
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //reference views once the view has been created
        tv_new_game_name.onFocusChangeListener = UIHelper.keyboardHider
        tv_new_game_time.onFocusChangeListener = UIHelper.keyboardHider

        btn_create.setOnClickListener { createGame() }
    }

    private fun createGame(){

        val timeLimit = tv_new_game_time.text.toString().trim()
        val playerName = tv_new_game_name.text.toString().trim()
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

        viewModel.currentUser = playerName
        createGame(Game("",chosenPacks,false,
            mutableListOf(playerName) as ArrayList, ArrayList(),timeLimit.toLong()))
    }

    private fun createGame(game: Game){
        //this is just for default data,in the future changes will be pulled from firebase
        viewModel.gameObject.value = game
        viewModel.ACCESS_CODE = UUID.randomUUID().toString().substring(0,6).toLowerCase()
        viewModel.gameRef.set(game)
            .addOnCompleteListener {
                val bundle = bundleOf("FromFragment" to "NewGameFragment")
                Navigation.findNavController(view!!).navigate(R.id.action_newGameFragment_to_waitingFragment,bundle)
            }
    }

}
