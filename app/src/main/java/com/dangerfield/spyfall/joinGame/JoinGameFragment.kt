package com.dangerfield.spyfall.joinGame

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.dangerfield.spyfall.customClasses.UIHelper
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.game.GameViewModel
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.fragment_join_game.btn_join_game_action
import kotlinx.android.synthetic.main.fragment_join_game.tv_access_code
import kotlinx.android.synthetic.main.fragment_join_game.tv_username

class JoinGameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_join_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_join_game_action.setOnClickListener { joinGameClick() }

        //listeners to hide keyboard when user clicks away
        tv_access_code.onFocusChangeListener = UIHelper.keyboardHider
        tv_username.onFocusChangeListener = UIHelper.keyboardHider
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)
    }

    fun joinGameClick(){

        val accessCode = tv_access_code.text.toString().trim()
        val userName = tv_username.text.toString().trim()

        if(userName.isEmpty() || accessCode.isEmpty()){
            Toast.makeText(context, "Please fill out both access code and user name", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.db.collection("games").document(accessCode).get().addOnSuccessListener { game ->
            if(game.exists()){
                val list = (game["playerList"] as ArrayList<String>)

                when {
                    list.size >= 8 ->  Toast.makeText(context, "Sorry, the max for a game is currently 8 players", Toast.LENGTH_LONG).show()
                    game["isStarted"]==true -> Toast.makeText(context, "Sorry, this game has been started", Toast.LENGTH_LONG).show()
                    list.contains(tv_username.text.toString().trim()) -> Toast.makeText(context, "Sorry, that name is taken by another player", Toast.LENGTH_LONG).show()
                    else -> joinGame(withAccessCode = accessCode, asPlayer = userName)
                }
            }else{
                Toast.makeText(context, "Sorry, no game was found with that access code", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun joinGame(withAccessCode: String, asPlayer: String){
        viewModel.db.collection("games").document(withAccessCode)
            .update("playerList", FieldValue.arrayUnion(asPlayer))

        viewModel.ACCESS_CODE = withAccessCode
        viewModel.currentUser = asPlayer
        Navigation.findNavController(view!!).navigate(R.id.action_joinGameFragment_to_waitingFragment)
    }
}
