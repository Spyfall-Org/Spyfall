package com.dangerfield.spyfall.waiting

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.customClasses.UIHelper
import com.dangerfield.spyfall.game.GameViewModel

import kotlinx.android.synthetic.main.fragment_waiting.*
import java.util.ArrayList

class WaitingFragment : Fragment() {

    private var adapter: WaitingPlayersAdapter? = null
    lateinit var viewModel: GameViewModel
    private var isGameCreator: Boolean = false
    private var navigateBack: (() -> Unit)? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isGameCreator = arguments?.get("FromFragment") == "NewGameFragment"
        return inflater.inflate(R.layout.fragment_waiting, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigateBack = {
             UIHelper.simpleAlert(context!!,"Leaving Game","Are you sure you want to leave?",
                "Leave", {leaveGame()},"Stay",{}).show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    navigateBack?.invoke()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController =  Navigation.findNavController(parentFragment!!.view!!)

        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        //made the owner this such that when the fragment dies, the observer goes with it
        viewModel.getGameUpdates().observe(this, Observer { updatedGame ->

            adapter?.players = updatedGame.playerList

            if(updatedGame.started && navController.currentDestination?.id == R.id.waitingFragment){
                navController.navigate(R.id.action_waitingFragment_to_gameFragment)
            }
        })

        tv_acess_code.text = viewModel.ACCESS_CODE
        configureLayoutManagerAndRecyclerView()

        btn_start_game.setOnClickListener {
            //only the game creator has the roles automatically
            if(viewModel.roles.isEmpty()){ viewModel.assignRolesAndStartGame() }else{ viewModel.startGame() }
        }

        btn_leave_game.setOnClickListener {navigateBack?.invoke() ?: leaveGame()}

        if(isGameCreator){ viewModel.getRandomLocation() }

    }

    private fun leaveGame(){
        viewModel.removePlayer().addOnCompleteListener {
            //once the database is updated check to see if game should be over
            if(viewModel.gameObject.value?.playerList?.size == 0){
                viewModel.endGame()
            }
            //pop the back stack all the way back to the start screen
            navController.popBackStack(R.id.startFragment,false)
        }
    }

    private fun configureLayoutManagerAndRecyclerView() {
            rv_player_list_waiting.layoutManager = LinearLayoutManager(context)
            adapter = WaitingPlayersAdapter(context!!, ArrayList())
            rv_player_list_waiting.adapter = adapter
    }
}
