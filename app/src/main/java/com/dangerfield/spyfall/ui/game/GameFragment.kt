package com.dangerfield.spyfall.ui.game

import android.content.res.Configuration
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Player
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import com.dangerfield.spyfall.util.LogHelper
import com.dangerfield.spyfall.util.EventObserver
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.util.getViewModelFactory
import com.google.android.gms.ads.AdRequest

class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var locationsAdapter: GameViewsAdapter
    private lateinit var playersAdapter: GameViewsAdapter
    private var changingTheme = false
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private val gameViewModel: GameViewModel by viewModels { getViewModelFactory(requireArguments()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(gameViewModel)

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //show alert when user presses back
                    UIHelper.customSimpleAlert(context!!,
                        getString(R.string.leave_game_title),
                        getString(R.string.leave_in_game_message),
                        getString(R.string.leave_action_positive),
                        { triggerEndGame() }, getString(R.string.leave_action_negative), {}).show()
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeGameUpdates()
        observeSessionEndedEvent()
        observeTimeLeft()
        observeGameLeftEvent()
        observeRemovedInactiveUserEvent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onResume() {
        super.onResume()

        if (gameViewModel.playAgainWasTriggered() && navController.currentDestination?.id == R.id.gameFragment) {
            //then user returned to the game but the game has been reset
            LogHelper.logUserResumedGameAfterPlayAgainTriggered(gameViewModel.currentSession)
            navController.popBackStack(R.id.waitingFragment, false)
        }
    }

    private fun observeGameUpdates() {
        gameViewModel.getLiveGame().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            gameViewModel.currentSession.game = it
            //play again has been triggered
            if (!it.started && navController.currentDestination?.id == R.id.gameFragment) {
                LogHelper.logPlayAgainTriggered(gameViewModel.currentSession)
                navController.popBackStack(R.id.waitingFragment, false)
            }

            //normal updates
            if (it.started && navController.currentDestination?.id == R.id.gameFragment) {
                //but right now it says: if the game has started, and im still on this screen, and something has changed..
                configurePlayerViews(it)
                configurePlayersAdapter(
                    it.playerObjectList,
                    it.playerList.shuffled() as ArrayList<String>
                )
                configureLocationsAdapter(it.locationList)
            }

            //user left the game as the game was starting
            if (it.started
                && it.playerObjectList.size != it.playerList.size
                && navController.currentDestination?.id == R.id.gameFragment
            ) {
                triggerReassign()
            }
        })
    }

    private fun observeSessionEndedEvent() {
        gameViewModel.getSessionEnded().observe(viewLifecycleOwner, EventObserver {
            if (navController.currentDestination?.id == R.id.gameFragment) {
                LogHelper.logSessionEndedInGame(gameViewModel.currentSession)
                handleEndGame()
            }
        })
    }

    private fun observeTimeLeft() {
        gameViewModel.getTimeLeft()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { time ->
                tv_game_timer.text = time
                btn_play_again.visibility =
                    if (time == GameViewModel.timeOver) View.VISIBLE else View.GONE
            })
    }

    private fun observeGameLeftEvent() {
        gameViewModel.getLeaveGameEvent().observe(viewLifecycleOwner, EventObserver {
            if (navController.currentDestination?.id == R.id.gameFragment) {
                if (it is Resource.Success) navigateToStart()
            }
        })
    }

    private fun observeRemovedInactiveUserEvent() {
        gameViewModel.getRemoveInactiveUserEvent().observe(viewLifecycleOwner, EventObserver {
            if (navController.currentDestination?.id == R.id.gameFragment && it is Resource.Success) {
                LogHelper.removedInactiveUser(gameViewModel.currentSession)
                navigateToStart()
            }
        })
    }

    fun triggerEndGame() {
        LogHelper.logUserTiggeredEndGame(gameViewModel.currentSession)
        gameViewModel.triggerEndGame()
    }

    //TODO add observer to listen elsehwere
    private fun triggerReassign() {
        Log.d("Elijah", "Reassigning roles")
        val startedGame = arguments?.getBoolean(WaitingFragment.STARTER) != null
        if(startedGame) gameViewModel.reassignRoles().observe(viewLifecycleOwner, EventObserver {
            when(it) {
                is Resource.Success -> {/* no-op. User will get updates */}
                is Resource.Error -> handleReassignError(it)
            }
        })
    }

    private fun handleReassignError(e: Resource.Error<Unit, StartGameError>) {
        e.exception?.let { LogHelper.logStartGameError(it) }
        e.error?.let {
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
        triggerEndGame()
    }


    private fun handleEndGame() {
        LogHelper.logEndingGame(gameViewModel.currentSession)
        navigateToStart()
    }

    private fun setupView() {
        tv_game_role.maxTextSize = 96.0f

        if (BuildConfig.FLAVOR == "free") {
            val adRequest = AdRequest.Builder().build()
            adView2.loadAd(adRequest)
        } else adView2.visibility = View.GONE

        changeAccent()

        btn_end_game.setOnClickListener {
            if (tv_game_timer.text.toString() == GameViewModel.timeOver) triggerEndGame()
            else {
                UIHelper.customSimpleAlert(requireContext(),
                    getString(R.string.end_game_title),
                    getString(R.string.end_game_message),
                    getString(R.string.end_game_positive_action), { triggerEndGame() },
                    getString(R.string.negative_action_standard), {}).show()
            }
        }

        btn_play_again.setOnClickListener {
            LogHelper.logUserClickedPlayAgain(gameViewModel.currentSession)
            //TODO change to trigger reset and handle errors
            gameViewModel.resetGame()
        }
        btn_hide.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btn_hide.setOnClickListener { hide() }

        tv_game_timer.text = String.format(
            Locale.getDefault(), "%d:%02d",
            gameViewModel.currentSession.game.timeLimit, 0
        )

        tv_game_timer.visibility =
            arguments?.getBoolean(WaitingFragment.NAVIGATED_USING_SAVED_SESSION_TO_STARTED_GAME)
                ?.let {
                    if ((it)) {
                        View.INVISIBLE
                    } else View.VISIBLE
                } ?: View.VISIBLE
    }

    private fun hide() {
        if (tv_game_role.visibility == View.VISIBLE) {
            tv_game_role.visibility = View.GONE
            tv_game_location.visibility = View.GONE
            view_role_card.visibility = View.GONE
            btn_hide.text = resources.getString(R.string.string_show)
        } else {
            tv_game_role.visibility = View.VISIBLE
            view_role_card.visibility = View.VISIBLE
            tv_game_location.visibility = View.VISIBLE
            btn_hide.text = resources.getString(R.string.string_hide)
        }
    }

    private fun navigateToStart() {
        gameViewModel.stopTimer()
        navController.popBackStack(R.id.startFragment, false)
    }

    private fun configureLocationsAdapter(locations: ArrayList<String>) {
        locationsAdapter = GameViewsAdapter(requireContext(), ArrayList(), null)
        rv_locations.apply {
            adapter = locationsAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        locationsAdapter.items = locations
    }

    private fun configurePlayersAdapter(playerObjects: List<Player>, players: ArrayList<String>) {
        val firstPlayer = findFirstPlayer(playerObjects, players)
        rv_players.apply {
            layoutManager = GridLayoutManager(context, 2)
            playersAdapter = GameViewsAdapter(context, players, firstPlayer)
            adapter = playersAdapter
            setHasFixedSize(true)
        }
    }

    private fun findFirstPlayer(playerObjects: List<Player>, players: java.util.ArrayList<String>): String? {
        playerObjects.forEach {playerObject ->
            val username = players.find { playerObject.username == it }
            if(username != null) return username
        }
        return ""
    }

    private fun configurePlayerViews(game: Game) {
        val currentPlayer =
            (game.playerObjectList).find { it.username == gameViewModel.currentSession.currentUser }
                ?: (game.playerObjectList).find { it.username == gameViewModel.currentSession.previousUserName }

        if (currentPlayer == null) {
            Log.d("Elijah", "BIG OLE NAME CHANGE ERROR: ${gameViewModel.currentSession}")
                Toast.makeText(context, getString(R.string.name_change_end_game), Toast.LENGTH_LONG)
                    .show()
                triggerEndGame()
            return
        }

        currentPlayer.let {
            tv_game_location.text = if (it.role == Constants.GameFields.theSpyRole) {
                "Figure out the location!"
            } else {
                "Location: ${game.chosenLocation}"
            }

            tv_game_role.text = "Role: ${it.role}"
        }
    }

    private fun changeAccent() {
        btn_end_game.background.setTint(UIHelper.accentColor)
        btn_hide.background.setTint(UIHelper.accentColor)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changingTheme = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!changingTheme) {
            gameViewModel.stopTimer()
        }
        changingTheme = false
    }
}
