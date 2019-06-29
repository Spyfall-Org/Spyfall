package com.dangerfield.spyfall.waiting

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.game.GameViewModel
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.fragment_waiting.*
import java.util.ArrayList

class WaitingFragment : Fragment() {

    private var adapter: WaitingPlayersAdapter? = null
    lateinit var viewModel: GameViewModel
    private var isGameCreator: Boolean = false
    private var navigateBack: (() -> Unit)? = null
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_waiting, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController =  Navigation.findNavController(parentFragment!!.view!!)
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        navigateBack = { UIHelper.customSimpleAlert(context!!,
            resources.getString(R.string.waiting_leaving_title),
            resources.getString(R.string.waiting_leaving_message),
            resources.getString(R.string.leave_action_positive), {leaveGame()},
            resources.getString(R.string.leave_action_negative),{ btn_leave_game.isClickable = true
            }).show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                   navigateBack?.invoke()
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getGameUpdates().observe(viewLifecycleOwner, Observer { updatedGame ->

            //if the game has been started, you cant click create
            btn_start_game.isClickable = !updatedGame.started

            adapter?.players = updatedGame.playerList

            //we know everything is good to go when the player objects list is done
            if(updatedGame.playerList.size == updatedGame.playerObjectList.size && navController.currentDestination?.id == R.id.waitingFragment){
                navController.navigate(R.id.action_waitingFragment_to_gameFragment)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeAccent()
        //only set the listeners once the view has been created
        btn_start_game.setOnClickListener {
            //TODO: Consider case of a user coming back to this screen, will the button be clickable?
            //immediately stop the same user from sending the request twice
            btn_start_game.isClickable = false
            //only the game creator has the roles automatically
            if(viewModel.roles.isEmpty()){ viewModel.getRolesAndStartGame() }else{ viewModel.startGame() }
        }

        btn_leave_game.setOnClickListener {
            btn_leave_game.isClickable = false
            navigateBack?.invoke() ?: leaveGame()
        }

        configureLayoutManagerAndRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // we need to check if the user is the game creator every time they come to this screen
        isGameCreator = arguments?.get("FromFragment") == "NewGameFragment"

        tv_acess_code.text = viewModel.ACCESS_CODE

        if(isGameCreator){ viewModel.getRandomLocation() }
    }

    private fun leaveGame(){
        viewModel.removePlayer().addOnCompleteListener {
            //once the database is updated check to see if game should be over
            if(viewModel.gameObject.value?.playerList?.size == 0){ viewModel.endGame()}
            //pop the back stack all the way back to the start screen
            navController.popBackStack(R.id.startFragment,false)
        }
    }

    private fun configureLayoutManagerAndRecyclerView() {
            rv_player_list_waiting.layoutManager = LinearLayoutManager(context)
            adapter = WaitingPlayersAdapter(context!!, ArrayList(),viewModel)
            rv_player_list_waiting.adapter = adapter
    }

    private fun changeAccent(){
        btn_leave_game.background.setTint(UIHelper.accentColor)
    }


    //you would think that the waiting view would not get destoryed if the game started == false. maybe jsut remove players if that happens
    override fun onPause() {
        super.onPause()
        Log.d("Waiting","onPause & started = ${viewModel.gameObject.value?.started}")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Waiting","onStop & started = ${viewModel.gameObject.value?.started}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Waiting","onDestroyView & started = ${viewModel.gameObject.value?.started}")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Waiting","onDestroy& started = ${viewModel.gameObject.value?.started}")
    }
}

