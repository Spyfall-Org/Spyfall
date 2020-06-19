package com.dangerfield.spyfall.joinGame

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.util.addCharacterMax
import kotlinx.android.synthetic.main.fragment_join_game.*
import org.koin.android.viewmodel.ext.android.viewModel

class JoinGameFragment : Fragment(R.layout.fragment_join_game) {

    private val joinGameViewModel: JoinGameViewModel by viewModel()
    private val navController: NavController by lazy {NavHostFragment.findNavController(this)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        updateAccent()
        btn_join_game_action.setOnClickListener { joinGameClick() }
        tv_access_code.onFocusChangeListener = UIHelper.keyboardHider
        tv_username.onFocusChangeListener = UIHelper.keyboardHider
        tv_access_code.addCharacterMax(8)
        tv_username.addCharacterMax(25)
    }

    private fun joinGameClick() {
        loadMode()
        val accessCode = tv_access_code.text.toString().toLowerCase().trim()
        val userName = tv_username.text.toString().trim()

        joinGameViewModel.joinGame(accessCode, userName).observe(this, Observer {
            if(!this.isAdded) return@Observer
            when (it) {
                is Resource.Success -> handleSuccessfulJoin()
                is Resource.Error -> showJoinGameError(it)
            }
        })
    }

    private fun handleSuccessfulJoin() {
        if(navController.currentDestination?.id != R.id.joinGameFragment) return
        enterMode()
        navController.navigate(R.id.action_joinGameFragment_to_waitingFragment )
    }

    private fun showJoinGameError(result: Resource.Error<Unit, JoinGameError>) {
        result.error?.let {error ->
            when(error) {
                JoinGameError.FIELD_ERROR ->
                    Toast.makeText(context, getString(R.string.join_game_error_fields), Toast.LENGTH_LONG).show()

                JoinGameError.NETWORK_ERROR ->
                    UIHelper.errorDialog(requireContext()).show()

                JoinGameError.GAME_DOES_NOT_EXIST ->
                    Toast.makeText(context, getString(R.string.join_game_error_access_code), Toast.LENGTH_LONG).show()

                JoinGameError.GAME_HAS_MAX_PLAYERS ->
                    Toast.makeText(context, getString(R.string.join_game_error_max_players), Toast.LENGTH_LONG).show()

                JoinGameError.GAME_HAS_STARTED ->
                    Toast.makeText(context, getString(R.string.join_game_error_started_game), Toast.LENGTH_LONG).show()

                JoinGameError.NAME_TAKEN ->
                    Toast.makeText(context, getString(R.string.join_game_error_taken_name), Toast.LENGTH_LONG).show()

                JoinGameError.NAME_CHARACTER_LIMIT ->
                    Toast.makeText(context, getString(R.string.change_name_character_limit), Toast.LENGTH_LONG).show()

                JoinGameError.COULD_NOT_JOIN ->
                    Toast.makeText(context, getString(R.string.join_game_error_could_not_join), Toast.LENGTH_LONG).show()

                JoinGameError.UNKNOWN_ERROR ->
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_LONG).show()
            }
        }
        enterMode()
    }

    private fun updateAccent(){
        btn_join_game_action.background.setTint(UIHelper.accentColor)
        UIHelper.setCursorColor(tv_access_code,UIHelper.accentColor)
        UIHelper.setCursorColor(tv_username,UIHelper.accentColor)
        pb_join_game.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN )
    }

    private fun loadMode(){
        btn_join_game_action.text = ""
        pb_join_game.visibility = View.VISIBLE
        btn_join_game_action.isClickable = false
    }

    private fun enterMode(){
        btn_join_game_action.text = getString(R.string.string_join_game)
        pb_join_game.visibility = View.INVISIBLE
        btn_join_game_action.isClickable = true
    }
}
