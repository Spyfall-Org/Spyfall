package com.dangerfield.spyfall.ui.start

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import kotlinx.android.synthetic.main.fragment_start.*
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import com.dangerfield.spyfall.util.EventObserver
import com.dangerfield.spyfall.util.ReviewHelper
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class StartFragment : Fragment(R.layout.fragment_start) {

    private val reviewHelper : ReviewHelper by inject()
    private val startViewModel : StartViewModel by viewModel()
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

        startViewModel.searchForUserInExistingGame()

        if (reviewHelper.shouldPromptForReview()) {
            UIHelper.customSimpleAlert(requireContext(),
                getString(R.string.dialog_rate_title),
                getString(R.string.dialog_rate_message),
                getString(R.string.positive_action_standard), {
                    reviewHelper.openStoreForReview()
                    reviewHelper.setHasClickedToReview()
                }, getString(R.string.dialog_rate_negative), {}).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeUserFoundInGameEvent()
    }

    private fun observeUserFoundInGameEvent() {
        startViewModel.getFoundUserInExistingGame().observe(viewLifecycleOwner, EventObserver {
            navigateToWaitingScreen(it.session, it.started)
        })
    }

    private fun setupView() {
        welcome_message.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

        btn_new_game.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_newGameFragment)
        }

        btn_join_game.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_joinGameFragment)
        }

        btn_rules.setOnClickListener {
            UIHelper.customSimpleAlert(requireContext(),
                resources.getString(R.string.rules_title),
                resources.getString(R.string.rules_message),
                resources.getString(R.string.positive_action_standard)
                , {}, "", {}, true
            ).show()
        }

        btn_settings.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_settingsFragment)
        }
    }

    private fun navigateToWaitingScreen(currentSession : Session, started: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(WaitingFragment.NAVIGATED_USING_SAVED_SESSION_TO_STARTED_GAME, started)
        bundle.putParcelable(WaitingFragment.SESSION_KEY, currentSession)
        navController.navigate(R.id.action_startFragment_to_waitingFragment, bundle)
    }

    private fun updateTheme() {
        btn_join_game.background.setTint(UIHelper.accentColor)

        val drawable = resources.getDrawable(R.drawable.ic_rules).mutate()
        drawable.setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_ATOP)
        btn_rules.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        btn_rules.setTextColor(UIHelper.accentColor)

        DrawableCompat.setTint(
            DrawableCompat.wrap(btn_settings.drawable),
            ContextCompat.getColor(requireContext(), R.color.colorTheme)
        )
    }
}
