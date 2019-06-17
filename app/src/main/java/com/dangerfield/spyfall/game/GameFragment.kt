package com.dangerfield.spyfall.game

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.models.Player
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.TypedValue
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.customClasses.UIHelper


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var currentPlayer: Player
    private lateinit var locationsAdapter: GameViewsAdapter
    private lateinit var timer: CountDownTimer
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("Game","onCreateView")
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Game","on Create")
        //this does not get called again, set all the variables you want to keep(not reassign)
        navController =  NavHostFragment.findNavController(this)
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        //the observers should not be called unless the fragment is in a started or resumed state
        viewModel.getGameUpdates().observe(this, androidx.lifecycle.Observer {
            //someone has reset game
            if(!it.started && navController.currentDestination?.id == R.id.gameFragment){
                viewModel.resetGame().addOnCompleteListener{
                    navController.popBackStack(R.id.waitingFragment, false)
                }
            }
        })

        /*
           1.) this code should not even be called as this fragment should not be getting recreated (popBack stack shouldnt call on destory)
           2.) the game should exist
           3.) the current destination should not be game fragment

           lets start by finding out why game fragment is at the top of this thing
           1.) tried seeting inclusive to true on the fragment stuff (didnt work)
           2.) make sure that everyone has the same nav controller and that when i pop the back stack that it changes the navcontroller (it does)

           3.) okay if I accept that the fragment dies every time and just gets recreated for no reason then what I can do is
           remove observers in on destroy (doesnt work because on resume gets called)
           //so two things, the fragment shouldnt get destoryed, and it shouldnt get recreated

           //it is as if im navigating to the fragment.

            */

        //so this observer is not getting detached, maybe manually detach it and reattach it in on resume
        viewModel.gameExists.observe(this, androidx.lifecycle.Observer { exists ->
            //someone ended the game
            var debug = navController
            if(!exists && navController.currentDestination?.id == R.id.gameFragment){ endGame() }
        })


        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    //show alert when user presses back
                    UIHelper.simpleAlert(context!!,"Can not leave game","If you chose to leave, the game will end",
                        "Leave", {endGame()},"Stay",{}).show()
                }
            })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //we set the listeners once the view has actually been inflated
        btn_end_game.setOnClickListener {endGame()}

        btn_play_again.setOnClickListener{
            viewModel.resetGame().addOnCompleteListener{
                navController.popBackStack(R.id.waitingFragment, false)
            }
        }
    }

    override fun onResume() {
        Log.d("GAME","ON RESUME")
        super.onResume()
        //just to be super safe
        if(viewModel.gameExists.value!!){
            //we enforce that no two users have the same username
            currentPlayer = (viewModel.gameObject.value!!.playerObjectList).filter { it.username == viewModel.currentUser }[0]

            timer = startTimer(viewModel.gameObject.value!!.timeLimit)

            //dont let the spy know the location
            tv_game_location.text = if(currentPlayer.role.toLowerCase().trim() == "the spy!"){
                "Figure out the location!"
            } else { "Location: ${viewModel.gameObject.value?.chosenLocation}" }

            tv_game_role.text = "Role: ${currentPlayer.role}"

            //if this is working, this should always be false
            var debug = viewModel.getAllLocations().hasActiveObservers()

            configurePlayersAdapter()
            configureLocationsAdapter()
        }
    }

    fun startTimer(timeLimit : Long): CountDownTimer {

        timer = object : CountDownTimer((60000*timeLimit), 1000) {
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
                showPlayAgain()
            }
        }.start()

        return timer
    }

    fun endGame(){
        viewModel.endGame()
        Log.d("Game", "current destination before crash: "+resources.getResourceEntryName(navController.currentDestination!!.id))
        timer.cancel()
        Log.d("Game", "current destination before poping: "+resources.getResourceEntryName(navController.currentDestination!!.id))
        navController.popBackStack(R.id.startFragment, false)
        Log.d("Game", "current destination after poping: "+resources.getResourceEntryName(navController.currentDestination!!.id))

    }

    private fun configureLocationsAdapter(){
        locationsAdapter = GameViewsAdapter(context!!, ArrayList())
        rv_locations.apply{
            adapter = locationsAdapter
            layoutManager = GridLayoutManager(context, 2) }

        //attaches observer after the locations adapter is actually initialized
        viewModel.getAllLocations().observe(this, androidx.lifecycle.Observer { locations ->
            locationsAdapter.items = locations
        })
    }

    private fun configurePlayersAdapter(){
        rv_players.apply{
            layoutManager = GridLayoutManager(context, 2)
            adapter = GameViewsAdapter(context,(viewModel.gameObject.value!!.playerObjectList.map { it.username }) as ArrayList<String>)
            setHasFixedSize(true)
        }
    }


    fun showPlayAgain(){

        val padding = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
            )
        )
        val set = ConstraintSet()
        val layout = game_layout as ConstraintLayout
        set.clone(layout)
        // The following breaks the connection.
        set.clear(R.id.btn_end_game, ConstraintSet.END)
        set.clear(R.id.btn_end_game, ConstraintSet.START)
        set.connect(R.id.btn_end_game,ConstraintSet.END,R.id.view_center,ConstraintSet.START,padding)
        set.applyTo(layout)
        btn_play_again.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("GAME","ON DESTROY")
        //TODO: consider destroynig game here
    }
}
