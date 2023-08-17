package com.dangerfield.spyfall.legacy.ui.joinGame

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.legacy.api.Resource
import com.dangerfield.spyfall.legacy.models.Session
import com.dangerfield.spyfall.legacy.ui.waiting.LegacyWaitingFragment
import com.dangerfield.spyfall.legacy.util.EventObserver
import com.dangerfield.spyfall.legacy.util.LogHelper
import com.dangerfield.spyfall.legacy.util.UIHelper
import com.dangerfield.spyfall.legacy.util.addCharacterMax
import com.dangerfield.spyfall.legacy.util.goneIf
import com.dangerfield.spyfall.legacy.util.setHideKeyBoardOnPressAway
import kotlinx.android.synthetic.main.fragment_join_game_legacy.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LegacyJoinGameFragment : Fragment(R.layout.fragment_join_game_legacy) {

    private val joinGameViewModel: JoinGameViewModel by viewModel()
    private val navController: NavController by lazy { NavHostFragment.findNavController(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    joinGameViewModel.cancelJoinGame()
                    navController.popBackStack(R.id.startFragment, false)
                }
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeJoinGameEvent()
    }

    private fun setupView() {
        updateTheme()
        btn_join_game_action.setOnClickListener { triggerJoinGame() }
        tv_access_code.setHideKeyBoardOnPressAway()
        tv_username.setHideKeyBoardOnPressAway()
        tv_access_code.addCharacterMax(8)
        tv_username.addCharacterMax(25)
    }

    private fun observeJoinGameEvent() {
        joinGameViewModel.getJoinGameEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is Resource.Success -> it.data?.let { session -> handleSuccessfulJoinGame(session) }
                    is Resource.Error -> handleErrorJoinGame(it)
                }
            }
        )
    }

    private fun triggerJoinGame() {
        showLoading(true)
        val accessCode = tv_access_code.text.toString().toLowerCase().trim()
        val userName = tv_username.text.toString().trim()
        joinGameViewModel.triggerJoinGame(accessCode, userName)
    }

    private fun handleSuccessfulJoinGame(currentSession: Session) {
        showLoading(false)
        val bundle = Bundle()
        bundle.putParcelable(LegacyWaitingFragment.SESSION_KEY, currentSession)
        if (navController.currentDestination?.id != R.id.joinGameFragment) return
        navController.navigate(R.id.action_joinGameFragment_to_waitingFragment, bundle)
    }

    private fun handleErrorJoinGame(result: Resource.Error<Session, JoinGameError>) {
        showLoading(false)
        result.exception?.let { LogHelper.logErrorJoiningGame(it) }
        result.error?.let { error ->
            if (error == JoinGameError.NETWORK_ERROR) {
                UIHelper.errorDialog(requireContext()).show()
            } else {
                error.resId?.let {
                    Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateTheme() {
        btn_join_game_action.background.setTint(UIHelper.accentColor)
        UIHelper.updateDrawableToTheme(requireContext(), R.drawable.edit_text_custom_cursor)
        pb_join_game.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    private fun showLoading(loading: Boolean) {
        btn_join_game_action.text = if (loading) "" else getString(R.string.string_join_game)
        btn_join_game_action.isClickable = !loading
        pb_join_game.goneIf(!loading)
    }
}
