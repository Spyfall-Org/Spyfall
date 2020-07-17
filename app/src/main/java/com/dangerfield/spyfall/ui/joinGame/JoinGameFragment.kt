package com.dangerfield.spyfall.ui.joinGame

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
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.util.addCharacterMax
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import com.dangerfield.spyfall.util.CrashlyticsLogger
import kotlinx.android.synthetic.main.fragment_join_game.*
import org.koin.android.viewmodel.ext.android.viewModel

class JoinGameFragment : Fragment(R.layout.fragment_join_game) {

    private val joinGameViewModel: JoinGameViewModel by viewModel()
    private val navController: NavController by lazy { NavHostFragment.findNavController(this) }

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

        joinGameViewModel.joinGame(accessCode, userName).observe(viewLifecycleOwner, Observer {
            if (!this.isAdded) return@Observer
            when (it) {
                is Resource.Success -> it.data?.let { session -> handleSuccessfulJoinGame(session) }
                is Resource.Error -> handleErrorJoinGame(it)
            }
        })
    }

    private fun handleSuccessfulJoinGame(currentSession: Session) {
        val bundle = Bundle()
        bundle.putParcelable(WaitingFragment.SESSION_KEY, currentSession)
        if (navController.currentDestination?.id != R.id.joinGameFragment) return
        enterMode()
        navController.navigate(R.id.action_joinGameFragment_to_waitingFragment, bundle)
    }

    private fun handleErrorJoinGame(result: Resource.Error<Session, JoinGameError>) {
        result.exception?.let { CrashlyticsLogger.logErrorJoiningGame(it) }

        result.error?.let {error ->
            if (error == JoinGameError.NETWORK_ERROR) {
                UIHelper.errorDialog(requireContext()).show()
            } else {
                error.resId?.let {
                    Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
                }
            }
        }
        enterMode()
    }

    private fun updateAccent() {
        btn_join_game_action.background.setTint(UIHelper.accentColor)
        UIHelper.setCursorColor(tv_access_code, UIHelper.accentColor)
        UIHelper.setCursorColor(tv_username, UIHelper.accentColor)
        pb_join_game.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    private fun loadMode() {
        btn_join_game_action.text = ""
        pb_join_game.visibility = View.VISIBLE
        btn_join_game_action.isClickable = false
    }

    private fun enterMode() {
        btn_join_game_action.text = getString(R.string.string_join_game)
        pb_join_game.visibility = View.INVISIBLE
        btn_join_game_action.isClickable = true
    }
}
