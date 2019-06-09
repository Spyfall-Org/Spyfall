package com.dangerfield.spyfall.joinGame

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.dangerfield.spyfall.CustomClasses.UIHelper

import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.fragment_join_game.*

class JoinGameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_join_game, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_join_game_action.setOnClickListener {
            joinGame(it)
        }

        //listeners to hide keyboard when user clicks away
        tv_access_code.onFocusChangeListener = UIHelper.keyboardHider
        tv_username.onFocusChangeListener = UIHelper.keyboardHider

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)
    }



    fun joinGame(sender: View){

        val accessCode = tv_access_code.text.trim()
        val userName = tv_username.text.trim()

        if(userName.isEmpty() || accessCode.isEmpty()){
            Toast.makeText(context, "Please fill out both access code and user name", Toast.LENGTH_LONG).show()
            return
        }

        //TODO: Let user join game using new MVVM
    }

}
