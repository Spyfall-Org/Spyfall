package com.dangerfield.spyfall.legacy.ui.game

import android.content.res.Configuration
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.FragmentGameLegacyBinding
import com.dangerfield.spyfall.legacy.api.Constants
import com.dangerfield.spyfall.legacy.api.Resource
import com.dangerfield.spyfall.legacy.models.Game
import com.dangerfield.spyfall.legacy.models.Player
import com.dangerfield.spyfall.legacy.ui.waiting.LeaveGameError
import com.dangerfield.spyfall.legacy.ui.waiting.LegacyWaitingFragment
import com.dangerfield.spyfall.legacy.util.EventObserver
import com.dangerfield.spyfall.legacy.util.LogHelper
import com.dangerfield.spyfall.legacy.util.UIHelper
import com.dangerfield.spyfall.legacy.util.getViewModelFactory
import com.dangerfield.spyfall.legacy.util.viewBinding
import com.google.android.gms.ads.AdRequest
import java.util.*
import kotlin.collections.ArrayList

class LegacyGameFragment : Fragment(R.layout.fragment_game_legacy) {

    private lateinit var locationsAdapter: GameViewsAdapter
    private lateinit var playersAdapter: GameViewsAdapter
    private var changingTheme = false
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private val gameViewModel: GameViewModel by viewModels { getViewModelFactory(requireArguments()) }
    private val binding by viewBinding(FragmentGameLegacyBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(gameViewModel)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // show alert when user presses back
                    UIHelper.customSimpleAlert(
                        context!!,
                        getString(R.string.leave_game_title),
                        getString(R.string.leave_in_game_message),
                        getString(R.string.leave_action_positive),
                        { triggerEndGame() }, getString(R.string.leave_action_negative), {}
                    ).show()
                }
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeGameUpdates()
        observeSessionEndedEvent()
        observeTimeLeft()
        observeRemovedInactiveUserEvent()
        observeReassignEvent()
        observeCurrentUserPlayAgainEvent()
        observeCurrentUserEndedGame()
        observeLeaveGameEvent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun observeGameUpdates() {
        gameViewModel.getLiveGame().observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                gameViewModel.currentSession.game = it
                // play again has been triggered
                if (!it.started && navController.currentDestination?.id == R.id.gameFragment) {
                    navigateToWaiting()
                }

                // normal updates
                if (it.started && navController.currentDestination?.id == R.id.gameFragment) {
                    // but right now it says: if the game has started, \
                    // and im still on this screen, and something has changed..
                    configurePlayerViews(it)
                    configurePlayersAdapter(
                        it.playerObjectList,
                        it.playerList.shuffled() as ArrayList<String>
                    )
                    configureLocationsAdapter(it.locationList)
                }

                // user left the game as the game was starting
                if (it.started &&
                    it.playerObjectList.size != it.playerList.size &&
                    navController.currentDestination?.id == R.id.gameFragment
                ) {
                    triggerReassign()
                }
            }
        )
    }

    private fun observeSessionEndedEvent() {
        gameViewModel.getSessionEnded().observe(
            viewLifecycleOwner,
            EventObserver {
                if (navController.currentDestination?.id == R.id.gameFragment) {
                    LogHelper.logSessionEndedInGame(gameViewModel.currentSession)
                    handleSessionEnded()
                }
            }
        )
    }

    private fun observeLeaveGameEvent() {
        gameViewModel.getLeaveGameEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is Resource.Success -> navigateToStart()
                    is Resource.Error -> handleLeaveGameError(it)
                }
            }
        )
    }

    private fun observeTimeLeft() {
        gameViewModel.getTimeLeft()
            .observe(
                viewLifecycleOwner
            ) { time ->
                binding.tvGameTimer.text = time
                binding.btnPlayAgain.visibility =
                    if (time == GameViewModel.timeOver) View.VISIBLE else View.GONE
            }
    }

    private fun observeRemovedInactiveUserEvent() {
        gameViewModel.getRemoveInactiveUserEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                if (navController.currentDestination?.id == R.id.gameFragment && it is Resource.Success) {
                    LogHelper.removedInactiveUser(gameViewModel.currentSession)
                    navigateToStart()
                }
            }
        )
    }

    private fun observeReassignEvent() {
        gameViewModel.getReassignEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is Resource.Success -> noop() /* updates to data are listened to in game observer */
                    is Resource.Error -> handleReassignError(it)
                }
            }
        )
    }

    private fun observeCurrentUserPlayAgainEvent() {
        gameViewModel.getPlayAgainEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is Resource.Success -> noop() /*play again will cause global trigger */
                    is Resource.Error -> handlePlayAgainError(it)
                }
            }
        )
    }

    private fun observeCurrentUserEndedGame() {
        gameViewModel.getCurrentUserEndedGameEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is Resource.Success -> noop() /*will cause global trigger*/
                    is Resource.Error -> navigateToStart()
                }
            }
        )
    }

    fun triggerEndGame() {
        LogHelper.logUserTiggeredEndGame(gameViewModel.currentSession)
        gameViewModel.triggerEndGame()
    }

    private fun triggerReassign() {
        val currentUserStatedGame = arguments?.getBoolean(LegacyWaitingFragment.STARTER) != null
        if (currentUserStatedGame) gameViewModel.triggerReassignRoles()
    }

    private fun triggerPlayAgain() {
        LogHelper.logUserClickedPlayAgain(gameViewModel.currentSession)
        gameViewModel.triggerPlayAgain()
    }

    private fun handleReassignError(e: Resource.Error<Unit, StartGameError>) {
        e.exception?.let { LogHelper.logStartGameError(it) }
        e.error?.let {
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
        triggerEndGame()
    }

    private fun handlePlayAgainError(e: Resource.Error<Unit, PlayAgainError>) {
        e.exception?.let { LogHelper.logErrorPlayAgain(it) }
        e.error?.let {
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLeaveGameError(e: Resource.Error<Unit, LeaveGameError>) {
        e.exception?.let { LogHelper.logLeaveGameError(it) }
        e.error?.let {
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSessionEnded() {
        LogHelper.logEndingGame(gameViewModel.currentSession)
        navigateToStart()
    }

    private fun setupView() {
        binding.tvGameRole.maxTextSize = 96.0f

        val adRequest = AdRequest.Builder().build()
        binding.adView2.loadAd(adRequest)

        changeAccent()

        binding.btnEndGame.setOnClickListener {
            if (binding.tvGameTimer.text.toString() == GameViewModel.timeOver) triggerEndGame()
            else {
                UIHelper.customSimpleAlert(
                    requireContext(),
                    getString(R.string.end_game_title),
                    getString(R.string.end_game_message),
                    getString(R.string.end_game_positive_action), { triggerEndGame() },
                    getString(R.string.negative_action_standard), {}
                ).show()
            }
        }

        binding.btnPlayAgain.setOnClickListener { triggerPlayAgain() }
        binding.btnHide.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.btnHide.setOnClickListener { hide() }

        binding.tvGameTimer.text = String.format(
            Locale.getDefault(), "%d:%02d",
            gameViewModel.currentSession.game.timeLimit, 0
        )

        binding.tvGameTimer.visibility =
            arguments?.getBoolean(LegacyWaitingFragment.NAVIGATED_USING_SAVED_SESSION_TO_STARTED_GAME)
                ?.let {
                    if ((it)) {
                        View.INVISIBLE
                    } else View.VISIBLE
                } ?: View.VISIBLE
    }

    private fun hide() {
        with(binding) {
            if (tvGameRole.visibility == View.VISIBLE) {
                tvGameRole.visibility = View.GONE
                tvGameLocation.visibility = View.GONE
                viewRoleCard.visibility = View.GONE
                btnHide.text = resources.getString(R.string.string_show)
            } else {
                tvGameRole.visibility = View.VISIBLE
                viewRoleCard.visibility = View.VISIBLE
                tvGameLocation.visibility = View.VISIBLE
                btnHide.text = resources.getString(R.string.string_hide)
            }
        }
    }

    private fun navigateToStart() {
        gameViewModel.stopTimer()
        navController.popBackStack(R.id.startFragment, false)
    }

    private fun navigateToWaiting() {
        gameViewModel.stopTimer()
        navController.popBackStack(R.id.waitingFragment, false)
    }

    private fun configureLocationsAdapter(locations: ArrayList<String>) {
        locationsAdapter = GameViewsAdapter(requireContext(), ArrayList(), null)
        binding.rvLocations.apply {
            adapter = locationsAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        locationsAdapter.items = locations
    }

    private fun configurePlayersAdapter(playerObjects: List<Player>, players: ArrayList<String>) {
        val firstPlayer = findFirstPlayer(playerObjects, players)
        binding.rvPlayers.apply {
            layoutManager = GridLayoutManager(context, 2)
            playersAdapter = GameViewsAdapter(context, players, firstPlayer)
            adapter = playersAdapter
            setHasFixedSize(true)
        }
    }

    private fun findFirstPlayer(playerObjects: List<Player>, players: java.util.ArrayList<String>): String =
        playerObjects.find { playerObject -> players.contains(playerObject.username) }?.username ?: ""

    private fun configurePlayerViews(game: Game) {
        val currentPlayer =
            (game.playerObjectList).find { it.username == gameViewModel.currentSession.currentUser }
                ?: (game.playerObjectList).find { it.username == gameViewModel.currentSession.previousUserName }

        if (currentPlayer == null) {
            navigateToStart()
            return
        }

        currentPlayer.let {
            binding.tvGameLocation.text = if (it.role == Constants.GameFields.theSpyRole) {
                "Figure out the location!"
            } else {
                "Location: ${game.chosenLocation}"
            }

            binding.tvGameRole.text = "Role: ${it.role}"
        }
    }

    private fun changeAccent() {
        binding.btnEndGame.background.setTint(UIHelper.accentColor)
        binding.btnHide.background.setTint(UIHelper.accentColor)
    }

    private fun noop() = Unit

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
