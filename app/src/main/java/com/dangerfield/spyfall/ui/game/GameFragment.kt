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
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import com.dangerfield.spyfall.util.CrashlyticsLogger
import com.dangerfield.spyfall.util.EventObserver
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.util.getViewModelFactory
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize

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

        tv_game_role.maxTextSize = 96.0f

        if (BuildConfig.FLAVOR == "free") {
            val adRequest = AdRequest.Builder().build()
            adView2.loadAd(adRequest)
        } else adView2.visibility = View.GONE

        gameViewModel.getLiveGame().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            //play again has been triggered
            if (!it.started && navController.currentDestination?.id == R.id.gameFragment) {
                CrashlyticsLogger.logPlayAgainTriggered(gameViewModel.currentSession)
                navController.popBackStack(R.id.waitingFragment, false)
            }

            if (it.started && navController.currentDestination?.id == R.id.gameFragment) {
                //but right now it says: if the game has started, and im still on this screen, and something has changed..
                configurePlayerViews(it)
                configurePlayersAdapter(
                    it.playerObjectList[0].username,
                    it.playerList.shuffled() as ArrayList<String>
                )
                configureLocationsAdapter(it.locationList)
            }

            if (it.started
                && it.playerObjectList.size != it.playerList.size
                && navController.currentDestination?.id == R.id.gameFragment
            ) {
                triggerEndGame()
            }

            gameViewModel.currentSession.game = it
        })

        gameViewModel.getSessionEnded().observe(viewLifecycleOwner, EventObserver {
            if (navController.currentDestination?.id == R.id.gameFragment) {
                CrashlyticsLogger.logSessionEndedInGame(gameViewModel.currentSession)
                handleEndGame()
            }
        })

        gameViewModel.getTimeLeft()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { time ->
                tv_game_timer.text = time
                btn_play_again.visibility =
                    if (time == GameViewModel.timeOver) View.VISIBLE else View.GONE
            })

        gameViewModel.getLeaveGameEvent().observe(viewLifecycleOwner, EventObserver {
            if (it is Resource.Success) handleEndGame()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeAccent()
        //we set the listeners once the view has actually been inflated
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
            CrashlyticsLogger.logUserClickedPlayAgain(gameViewModel.currentSession)
            gameViewModel.resetGame()
        }
        btn_hide.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btn_hide.setOnClickListener { hide() }

        tv_game_timer.text = String.format(
            Locale.getDefault(), "%d:%02d",
            gameViewModel.currentSession.game.timeLimit, 0
        )

        tv_game_timer.visibility =
            arguments?.getBoolean(WaitingFragment.NAVIGATE_TO_STARTED_GAME_FLAG)?.let {
                if ((it)) {
                    View.INVISIBLE
                } else View.VISIBLE
            } ?: View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        if (gameViewModel.playAgainWasTriggered() && navController.currentDestination?.id == R.id.gameFragment) {
            //then user returned to the game but the game has been reset
            CrashlyticsLogger.logUserResumedGameAfterPlayAgainTriggered(gameViewModel.currentSession)
            navController.popBackStack(R.id.waitingFragment, false)
        }
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


    fun triggerEndGame() {
        CrashlyticsLogger.logUserTiggeredEndGame(gameViewModel.currentSession)
        gameViewModel.triggerEndGame()
    }

    private fun handleEndGame() {
        CrashlyticsLogger.logEndingGame(gameViewModel.currentSession)
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

    private fun configurePlayersAdapter(firstPlayer: String, players: ArrayList<String>) {
        rv_players.apply {
            layoutManager = GridLayoutManager(context, 2)
            playersAdapter = GameViewsAdapter(context, players, firstPlayer)
            adapter = playersAdapter
            setHasFixedSize(true)
        }
    }

    private fun configurePlayerViews(game: Game) {
        val currentPlayer =
            (game.playerObjectList).find { it.username == gameViewModel.currentSession.currentUser }
                ?: (game.playerObjectList).find { it.username == gameViewModel.currentSession.previousUserName }

        if (currentPlayer == null) {
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
