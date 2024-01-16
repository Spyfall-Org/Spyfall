package com.dangerfield.oddoneout.legacy.ui.start

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.oddoneout.R
import com.dangerfield.oddoneout.databinding.FragmentStartLegacyBinding
import com.dangerfield.oddoneout.legacy.api.Resource
import com.dangerfield.oddoneout.legacy.models.Session
import com.dangerfield.oddoneout.legacy.ui.waiting.LeaveGameError
import com.dangerfield.oddoneout.legacy.ui.waiting.LegacyWaitingFragment
import com.dangerfield.oddoneout.legacy.util.EventObserver
import com.dangerfield.oddoneout.legacy.util.LogHelper
import com.dangerfield.oddoneout.legacy.util.ReviewHelper
import com.dangerfield.oddoneout.legacy.util.UIHelper
import com.dangerfield.oddoneout.legacy.util.viewBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LegacyStartFragment : Fragment(R.layout.fragment_start_legacy) {

    private val binding by viewBinding(FragmentStartLegacyBinding::bind)
    private val reviewHelper: ReviewHelper by inject()
    private val startViewModel: StartViewModel by viewModel()
    private val navController by lazy {
        NavHostFragment.findNavController(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        UIHelper.getSavedColor(requireContext())
        updateTheme()

        startViewModel.triggerSearchForUserInExistingGame()
        if (reviewHelper.shouldPromptForReview()) {
            showReviewDialog()
        }
    }

    private fun showReviewDialog() {
        UIHelper.getReviewDialog(requireContext(),
            positiveAction = {
                reviewHelper.setHasClickedToReview()
                reviewHelper.openStoreForReview()
            }).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeUserFoundInGameEvent()
        observeLeaveGameEvent()
    }

    private fun observeLeaveGameEvent() {
        startViewModel.getLeaveGameEvent().observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is Resource.Success -> {/*noop*/
                }

                is Resource.Error -> {
                    handleLeaveGameError(it)
                }
            }
        })
    }

    private fun observeUserFoundInGameEvent() {
        startViewModel.getSearchForUserInGameEvent().observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is Resource.Success -> it.data?.let { d -> getPreviousGameDialog(d).show() }
                is Resource.Error -> {
                } //noop
            }
        })
    }

    private fun handleLeaveGameError(result: Resource.Error<Unit, LeaveGameError>) {
        result.exception?.let { LogHelper.logLeaveGameError(it) }
        result.error?.let {
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun triggerLeaveGame(savedSession: SavedSession) {
        startViewModel.removeUserFromSession(savedSession.session)
    }

    private fun getPreviousGameDialog(savedSession: SavedSession): Dialog {
        return UIHelper.customSimpleAlert(requireContext(),
            getString(R.string.enter_previous_game),
            getString(R.string.enter_previous_game_message),
            getString(R.string.yes),
            { navigateToWaitingScreen(savedSession.session, savedSession.started) },
            getString(R.string.no),
            { triggerLeaveGame(savedSession) }
        )
    }

    private fun setupView() {
        binding.apply {
            welcomeMessage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

            btnNewGame.setOnClickListener {
                navController.navigate(R.id.action_startFragment_to_newGameFragment)
            }

            btnJoinGame.setOnClickListener {
                navController.navigate(R.id.action_startFragment_to_joinGameFragment)
            }

            btnRules.setOnClickListener { showRulesDialog() }

            btnSettings.setOnClickListener {
                navController.navigate(R.id.action_startFragment_to_settingsFragment)
            }
        }
    }

    private fun showRulesDialog() {
        UIHelper.customSimpleAlert(requireContext(),
            resources.getString(R.string.rules_title),
            resources.getString(R.string.rules_message),
            resources.getString(R.string.positive_action_standard), {}, "", {}, true
        ).show()
    }

    private fun navigateToWaitingScreen(currentSession: Session, started: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(
            LegacyWaitingFragment.NAVIGATED_USING_SAVED_SESSION_TO_STARTED_GAME,
            started
        )
        bundle.putParcelable(LegacyWaitingFragment.SESSION_KEY, currentSession)
        navController.navigate(R.id.action_startFragment_to_waitingFragment, bundle)
    }

    private fun updateTheme() {
        binding.apply {
            btnJoinGame.background.setTint(UIHelper.accentColor)
            btnRules.setTextColor(UIHelper.accentColor)

            DrawableCompat.setTint(
                DrawableCompat.wrap(btnSettings.drawable),
                ContextCompat.getColor(requireContext(), R.color.black)
            )

            DrawableCompat.setTint(
                DrawableCompat.wrap(ivRules.drawable),
                UIHelper.accentColor
            )
        }
    }
}
