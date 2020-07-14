package com.dangerfield.spyfall.waiting

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.util.EventObserver
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_waiting.*
import org.koin.android.viewmodel.ext.android.viewModel

class WaitingFragment : Fragment(R.layout.fragment_waiting), NameChangeEventFirer, CurrentUserHelper {

    private val adapter by lazy { WaitingPlayersAdapter(this, changeNameHelper) }
    private val waitingViewModel: WaitingViewModel by viewModel()
    private val changeNameHelper by lazy {ChangeNameHelper(this)}
    private var currentGame: Game? = null

    private fun showLeaveGameDialog() {
        UIHelper.customSimpleAlert(context!!,
            resources.getString(R.string.waiting_leaving_title),
            resources.getString(R.string.waiting_leaving_message),
            resources.getString(R.string.leave_action_positive), { leaveGame() },
            resources.getString(R.string.leave_action_negative), {}).show()
    }

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (BuildConfig.FLAVOR == "free") adView.loadAd(AdRequest.Builder().build())

        waitingViewModel.game?.observe(viewLifecycleOwner, Observer {
            if (!this.isAdded) return@Observer

            currentGame = it
            adapter.players = it.playerList

            if (it.started) loadMode() else enterMode()

            if (it.playerObjectList.size == it.playerList.size && navController.currentDestination?.id == R.id.waitingFragment) {
                enterMode()
                navController.navigate(R.id.action_waitingFragment_to_gameFragment)
            }
        })

        waitingViewModel.gameExists?.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { gameExists ->
                if (!gameExists && navController.currentDestination?.id == R.id.waitingFragment) {
                    navController.popBackStack(R.id.startFragment, false)
                }
            })

        waitingViewModel.getNameChangeEvent().observe(viewLifecycleOwner, EventObserver {
            when(it) {
                is Resource.Success -> { changeNameHelper.dismissNameChangeDialog() }
                is Resource.Error -> handleNameChangeError(it)
            }
        })
    }

    private fun handleNameChangeError(result: Resource.Error<String, NameChangeError>) {
        result.error?.let {
            when (it) {
                NameChangeError.GAME_STARTED,
                NameChangeError.UNKNOWN_ERROR,
                NameChangeError.NETWORK_ERROR -> changeNameHelper.dismissNameChangeDialog()
                else -> {} //no-op. keep dialog up so user can fix mistake
            }
            Toast.makeText(context, resources.getString(it.resId), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        changeAccent()
        btn_start_game.setOnClickListener {
            loadMode()
            waitingViewModel.startGame()
        }

        btn_leave_game.setOnClickListener { showLeaveGameDialog() }

        configureLayoutManagerAndRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        tv_acess_code.text = waitingViewModel.accessCode
    }

    private fun leaveGame() {
        if (currentGame?.started == true) return
        waitingViewModel.leaveGame()
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
        btn_leave_game.isClickable = false
        btn_start_game.isClickable = false
    }

    private fun enterMode() {
        btn_start_game.text = getString(R.string.string_btn_start_game)
        pb_waiting.visibility = View.GONE
        btn_leave_game.isClickable = true
        btn_start_game.isClickable = true
    }

    override fun fireNameChangeEvent(newName: String) {
        waitingViewModel.fireNameChange(newName)
    }

    override fun getCurrentUser(): String {
        return waitingViewModel.getCurrentUser()
    }
}

