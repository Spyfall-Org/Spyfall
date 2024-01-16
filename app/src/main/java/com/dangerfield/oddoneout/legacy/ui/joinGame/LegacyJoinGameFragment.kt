package com.dangerfield.oddoneout.legacy.ui.joinGame

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.oddoneout.R
import com.dangerfield.oddoneout.databinding.FragmentJoinGameLegacyBinding
import com.dangerfield.oddoneout.legacy.api.Resource
import com.dangerfield.oddoneout.legacy.models.Session
import com.dangerfield.oddoneout.legacy.ui.waiting.LegacyWaitingFragment
import com.dangerfield.oddoneout.legacy.util.EventObserver
import com.dangerfield.oddoneout.legacy.util.LogHelper
import com.dangerfield.oddoneout.legacy.util.UIHelper
import com.dangerfield.oddoneout.legacy.util.addCharacterMax
import com.dangerfield.oddoneout.legacy.util.goneIf
import com.dangerfield.oddoneout.legacy.util.setHideKeyBoardOnPressAway
import com.dangerfield.oddoneout.legacy.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LegacyJoinGameFragment : Fragment(R.layout.fragment_join_game_legacy) {

    private val binding by viewBinding(FragmentJoinGameLegacyBinding::bind)
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
        with(binding) {
            btnJoinGameAction.setOnClickListener { triggerJoinGame() }
            tvAccessCode.setHideKeyBoardOnPressAway()
            tvUsername.setHideKeyBoardOnPressAway()
            tvAccessCode.addCharacterMax(8)
            tvUsername.addCharacterMax(25)
        }
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
        val accessCode = binding.tvAccessCode.text.toString().toLowerCase().trim()
        val userName = binding.tvUsername.text.toString().trim()
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
        binding.btnJoinGameAction.background.setTint(UIHelper.accentColor)
        UIHelper.updateDrawableToTheme(requireContext(), R.drawable.edit_text_custom_cursor)
        binding.pbJoinGame.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    private fun showLoading(loading: Boolean) {
        with(binding) {
            btnJoinGameAction.text = if (loading) "" else getString(R.string.string_join_game)
            btnJoinGameAction.isClickable = !loading
            pbJoinGame.goneIf(!loading)
        }
    }
}
