package com.dangerfield.spyfall.waiting

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.customClasses.UIHelper
import com.dangerfield.spyfall.game.GameViewModel
import com.dangerfield.spyfall.models.Game
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_waiting.*
import java.util.ArrayList

class WaitingFragment : Fragment() {

    //TODO: handle back press deleting node and clearning view model and such

    private var adapter: WaitingPlayersAdapter? = null
    lateinit var viewModel: GameViewModel
    private var isGameCreator: Boolean = false
    private var navigateBack: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isGameCreator = arguments?.get("FromFragment") == "NewGameFragment"
        return inflater.inflate(R.layout.fragment_waiting, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigateBack = {
             UIHelper.simpleAlert(context!!,"Leaving","Are you sure you want to leave?",
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

        var navController =  Navigation.findNavController(parentFragment!!.view!!)


        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        viewModel.getGameUpdates().observe(activity!!, Observer { updatedGame ->
            if(updatedGame.playerList.size == 0){
                Log.d("END","END")
                viewModel.endGame()
            }
            adapter?.players = updatedGame.playerList

            if(updatedGame.started && navController.currentDestination?.id == R.id.waitingFragment){
                navController.navigate(R.id.action_waitingFragment_to_gameFragment)
              //  Navigation.findNavController(view).navigate(R.id.action_waitingFragment_to_gameFragment)
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
        //TODO: when a player leaves the game, the viewmodel still holds the databse reference, find a way to deal with this
        viewModel.removePlayer()
        //pop the back stack all the way back to the start screen
        var debug = fragmentManager
        Navigation.findNavController(view!!).popBackStack(R.id.startFragment,false)
    }



    private fun configureLayoutManagerAndRecyclerView() {
            rv_player_list_waiting.layoutManager = LinearLayoutManager(context)
            adapter = WaitingPlayersAdapter(context!!, ArrayList())
            rv_player_list_waiting.adapter = adapter
    }
}
