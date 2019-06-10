package com.dangerfield.spyfall.waiting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.game.GameViewModel
import kotlinx.android.synthetic.main.fragment_waiting.*
import java.util.ArrayList

class WaitingFragment : Fragment() {

    private var adapter: WaitingPlayersAdapter? = null
    lateinit var viewModel: GameViewModel
    private var isGameCreator: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_waiting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        isGameCreator = arguments?.get("FromFragment") == "NewGameFragment"

        tv_acess_code.text = viewModel.ACCESS_CODE
        configureLayoutManagerAndRecyclerView()

        btn_start_game.setOnClickListener {
            viewModel.startGame()
        }

        btn_leave_game.setOnClickListener { viewModel.removePlayer() }

        //gets called every time our view models player list value changes
        viewModel.getGameUpdates().observe(activity!!, Observer { updatedPlayers ->
            if(updatedPlayers.size == 0){
                Log.d("END","END")
                viewModel.endGame()
                Navigation.findNavController(view).navigate(R.id.action_waitingFragment_to_startFragment)
            }
            adapter?.players = updatedPlayers
        })

        viewModel.gameHasStarted.observe(activity!!, Observer { gameHasStarted ->
            if(gameHasStarted){
                Navigation.findNavController(this.view!!).navigate(R.id.action_waitingFragment_to_gameFragment)
            }
        })

        //you only want to be the one picking a location iff you started the game
        if(isGameCreator){
            viewModel.getRandomLocation().observe(activity!!, Observer {
                Log.d("Random Location:", it)
            })
        }

    }


    private fun configureLayoutManagerAndRecyclerView() {
            rv_player_list_waiting.layoutManager = LinearLayoutManager(context)
            adapter = WaitingPlayersAdapter(context!!, ArrayList())
            rv_player_list_waiting.adapter = adapter
    }
}
