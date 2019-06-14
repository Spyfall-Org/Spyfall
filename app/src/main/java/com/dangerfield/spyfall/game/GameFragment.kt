package com.dangerfield.spyfall.game


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager

import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.models.Player
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var currentPlayer: Player
    private lateinit var locationsAdapter: GameViewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        //we can garuntee there will be no two users with the same super name
        currentPlayer = (viewModel.gameObject.value!!.playerObjectList).filter { it.username == viewModel.currentUser }[0]

        var timer = startTimer(viewModel.gameObject.value!!.timeLimit)
        tv_game_location.text = viewModel.gameObject.value!!.chosenLocation
        tv_game_role.text = currentPlayer.role

        btn_end_game.setOnClickListener {
            viewModel.endGame()
            timer.cancel()
            var debug = fragmentManager
            Navigation.findNavController(view).popBackStack(R.id.startFragment, false)
        }

        configurePlayersAdapter()
        configureLocationsAdapter()

        viewModel.getAllLocations().observe(activity!!, androidx.lifecycle.Observer { locations ->
            locationsAdapter.items = locations
        })

    }


    fun startTimer(timeLimit : Long): CountDownTimer {

        val timer = object : CountDownTimer((60000*timeLimit), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                )
                tv_game_timer.text = text
            }

            override fun onFinish() {
                tv_game_timer.text = "done!"
            }
        }.start()

        return timer

    }

    fun configureLocationsAdapter(){
        locationsAdapter = GameViewsAdapter(context!!, ArrayList())
        rv_locations.apply{
            adapter = locationsAdapter
            layoutManager = GridLayoutManager(context, 2) }
    }

    fun configurePlayersAdapter(){
        rv_players.apply{
            layoutManager = GridLayoutManager(context, 2)
            adapter = GameViewsAdapter(context,(viewModel.gameObject.value!!.playerObjectList.map { it.username }) as ArrayList<String>)
            setHasFixedSize(true)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("Game", "OnDestory")
    }
}
