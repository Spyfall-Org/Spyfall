package com.dangerfield.spyfall.ui.waiting

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.util.LogHelper
import com.dangerfield.spyfall.util.EventObserver
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.util.getViewModelFactory
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_waiting.*

class WaitingFragment : Fragment(R.layout.fragment_waiting), NameChangeEventFirer {

    private val adapter by lazy {
        WaitingPlayersAdapter(
            waitingViewModel.currentSession.currentUser,
            changeNameHelper
        )
    }
    private val changeNameHelper by lazy { ChangeNameHelper(this) }
    private val waitingViewModel: WaitingViewModel by viewModels {
        getViewModelFactory(
            requireArguments()
        )
    }
    private val navigationBundle = Bundle()

    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showLeaveGameDialog()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeGameUpdates()
        observeNameChangeEvent()
        observeLeaveGameEvent()
        observeSessionEnded()
        observeInactiveUserRemoved()
    }

    private fun setupView() {
        changeAccent()
        btn_start_game.setOnClickListener { fireStartGame() }

        btn_leave_game.setOnClickListener { showLeaveGameDialog() }

        configureLayoutManagerAndRecyclerView()

        adView.visibility = if (BuildConfig.FLAVOR == "free") {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            View.VISIBLE
        } else View.GONE
    }

    private fun observeGameUpdates() {
        waitingViewModel.getLiveGame().observe(viewLifecycleOwner, Observer {
            if (!this.isAdded) return@Observer

            waitingViewModel.currentSession.game = it
            adapter.players = it.playerList
            tv_acess_code.text = waitingViewModel.currentSession.accessCode

            if (it.started) loadMode() else enterMode()

            if (it.playerObjectList.size > 0 && navController.currentDestination?.id == R.id.waitingFragment) {
                enterMode()
                navigateToGameScreen()
            }
        })
    }

    private fun observeNameChangeEvent() {
        waitingViewModel.getNameChangeEvent().observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is Resource.Success -> handleNameChangeSuccess(it)
                is Resource.Error -> handleNameChangeError(it)
            }
        })
    }

    private fun observeLeaveGameEvent() {
        waitingViewModel.getLeaveGameEvent().observe(viewLifecycleOwner, EventObserver {
            if (navController.currentDestination?.id == R.id.waitingFragment) {
                // handle leave game events differently in the game screen
                when (it) {
                    is Resource.Success -> navigateToStart()
                    is Resource.Error -> handleLeaveGameError(it)
                }
            }
        })
    }

    private fun observeSessionEnded() {
        waitingViewModel.getSessionEnded().observe(viewLifecycleOwner, EventObserver {
            if (navController.currentDestination?.id == R.id.waitingFragment) {
                LogHelper.logSessionEndedInWaiting(waitingViewModel.currentSession)
                navigateToStart()
            }
        })
    }

    private fun observeInactiveUserRemoved() {
        waitingViewModel.getRemoveInactiveUserEvent().observe(viewLifecycleOwner, EventObserver {
            if (navController.currentDestination?.id == R.id.waitingFragment && it is Resource.Success) {
                LogHelper.removedInactiveUser(waitingViewModel.currentSession)
                navigateToStart()
            }
        })
    }

    private fun handleLeaveGameError(result: Resource.Error<Unit, LeaveGameError>) {
        result.exception?.let { LogHelper.logLeaveGameError(it) }
        result.error?.let {
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNameChangeSuccess(it: Resource.Success<String, NameChangeError>) {
        it.data?.let {
            waitingViewModel.currentSession.updateCurrentUsername(it)
            waitingViewModel.currentSession.currentUser = it
            adapter.currentUserName = it
        }
        LogHelper.logSuccesfulNameChange(waitingViewModel.currentSession)
        changeNameHelper.dismissNameChangeDialog()
    }

    private fun handleNameChangeError(result: Resource.Error<String, NameChangeError>) {
        result.exception?.let { LogHelper.logNameChangeError(it) }
        result.error?.let {
            when (it) {
                NameChangeError.GAME_STARTED,
                NameChangeError.UNKNOWN_ERROR,
                NameChangeError.NETWORK_ERROR -> changeNameHelper.dismissNameChangeDialog()
                else -> {
                } //no-op. keep dialog up so user can fix mistake
            }
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun fireStartGame() {
        LogHelper.logUserClickedStartGame(waitingViewModel.currentSession)
        navigationBundle.putBoolean(STARTER, true)
        loadMode()
        waitingViewModel.fireStartGame().observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is Resource.Success -> {/*no-op player list change will trigger navigation*/
                }
                is Resource.Error -> handleStartGameError(it)
            }
        })
    }

    private fun handleStartGameError(e: Resource<Unit, StartGameError>) {
        e.exception?.let { LogHelper.logStartGameError(it) }
        e.error?.let {
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun fireLeaveGameEvent() {
        LogHelper.logUserClickedToLeaveGame(waitingViewModel.currentSession)
        waitingViewModel.fireLeaveGameEvent()
    }

    override fun fireNameChangeEvent(newName: String) {
        LogHelper.logUserChangingName(newName, waitingViewModel.currentSession)
        waitingViewModel.fireNameChange(newName)
    }

    private fun navigateToGameScreen() {
        LogHelper.logNavigatingToGameScreen(waitingViewModel.currentSession)
        changeNameHelper.dismissNameChangeDialog()
        /*
        We only want to do this once because if a user that navigated to game using
        saved session gets to play again, we want to make sure they can see the timer
         */
        arguments?.getBoolean(NAVIGATED_USING_SAVED_SESSION_TO_STARTED_GAME)?.let {
            if (!waitingViewModel.hasNavigatedUsingSavedSessionToStartedGame) {
                navigationBundle.putBoolean(NAVIGATED_USING_SAVED_SESSION_TO_STARTED_GAME, it)
                waitingViewModel.hasNavigatedUsingSavedSessionToStartedGame = true
            }
        }
        navigationBundle.putParcelable(SESSION_KEY, waitingViewModel.currentSession)
        navController.navigate(R.id.action_waitingFragment_to_gameFragment, navigationBundle)
    }

    private fun navigateToStart() {
        LogHelper.logSessionEndedInWaiting(waitingViewModel.currentSession)
        navController.popBackStack(R.id.startFragment, false)
    }

    private fun configureLayoutManagerAndRecyclerView() {
        rv_player_list_waiting.layoutManager = LinearLayoutManager(context)
        rv_player_list_waiting.adapter = adapter
    }

    private fun changeAccent() {
        btn_start_game.background.setTint(UIHelper.accentColor)
        pb_waiting.indeterminateDrawable
            .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    private fun loadMode() {
        btn_start_game.text = ""
        pb_waiting.visibility = View.VISIBLE
        btn_start_game.isClickable = false
    }

    private fun enterMode() {
        btn_start_game.text = getString(R.string.string_btn_start_game)
        pb_waiting.visibility = View.GONE
        btn_leave_game.isClickable = true
        btn_start_game.isClickable = true
    }

    private fun showLeaveGameDialog() {
        UIHelper.customSimpleAlert(requireContext(),
            resources.getString(R.string.waiting_leaving_title),
            resources.getString(R.string.waiting_leaving_message),
            resources.getString(R.string.leave_action_positive), { fireLeaveGameEvent() },
            resources.getString(R.string.leave_action_negative), {}).show()
    }

    companion object {
        const val SESSION_KEY = "123_pls_help_me"
        const val NAVIGATED_USING_SAVED_SESSION_TO_STARTED_GAME = "thisisasupercoolkey"
        const val STARTER = "thisisanothhersupercoolkey"
    }
}

