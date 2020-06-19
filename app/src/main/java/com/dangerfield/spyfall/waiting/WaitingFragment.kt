package com.dangerfield.spyfall.waiting

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.game.GameViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.fragment_waiting.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList

class WaitingFragment : Fragment(R.layout.fragment_waiting) {

    private val adapter by lazy { WaitingPlayersAdapter(requireContext(), ArrayList())}
    private val waitingViewModel: WaitingViewModel by viewModel()

    private val navigateBack by lazy {
        {
            UIHelper.customSimpleAlert(context!!,
                resources.getString(R.string.waiting_leaving_title),
                resources.getString(R.string.waiting_leaving_message),
                resources.getString(R.string.leave_action_positive), { leaveGame() },
                resources.getString(R.string.leave_action_negative), {}).show()
        }
    }

    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                   navigateBack.invoke()
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(BuildConfig.FLAVOR == "free") adView.loadAd(AdRequest.Builder().build())

        waitingViewModel.game?.observe(viewLifecycleOwner, Observer {
            if(!this.isAdded) return@Observer

            adapter.players = it.playerList
            if(it.started) loadMode() else enterMode()

            if(it.playerObjectList.size > 0 && navController.currentDestination?.id == R.id.waitingFragment) {
                Crashlytics.log("Navigating from waiting to game screen with game $it")
                enterMode()
                navController.navigate(R.id.action_waitingFragment_to_gameFragment)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        changeAccent()
        btn_start_game.setOnClickListener {
            loadMode()
          //  viewModel.getRolesAndStartGame()
        }

        btn_leave_game.setOnClickListener {
//            if(viewModel.gameObject.value?.started == false){
//                navigateBack?.invoke() ?: leaveGame()
//            }
        }

        configureLayoutManagerAndRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        tv_acess_code.text = waitingViewModel.accessCode
    }

    private fun leaveGame() {
       // viewModel.removePlayer()
        navController.popBackStack(R.id.startFragment, false)

    }
    private fun configureLayoutManagerAndRecyclerView() {
        rv_player_list_waiting.layoutManager = LinearLayoutManager(context)
        rv_player_list_waiting.adapter = adapter
    }

    private fun changeAccent(){
        btn_start_game.background.setTint(UIHelper.accentColor)
        pb_waiting.indeterminateDrawable
            .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN )
    }

    fun loadMode(){
        btn_start_game.text = ""
        pb_waiting.visibility = View.VISIBLE
        btn_leave_game.isClickable = false
        btn_start_game.isClickable = false
    }
    fun enterMode(){
        btn_start_game.text = getString(R.string.string_btn_start_game)
        pb_waiting.visibility = View.GONE
        btn_leave_game.isClickable = true
        btn_start_game.isClickable = true
    }

}

