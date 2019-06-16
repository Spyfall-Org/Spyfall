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
    private lateinit var endObserver: ((Boolean) -> Unit)
    private lateinit var timer: CountDownTimer
    private lateinit var navController: NavController
    private var navigateBack: (() -> Unit)? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Game", "On View Created Called In Game")

        navController =  NavHostFragment.findNavController(this)


        navigateBack = {
            UIHelper.simpleAlert(context!!,"Can not leave game","If you chose to leave, the game will end",
                "Leave", {endGame()},"Stay",{}).show()
        }

        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)


        endObserver = {exists ->
            //someone ended the game
            if(!exists && navController.currentDestination?.id == R.id.gameFragment){
                endGame()
            }
        }
        //i attatch the observer to the fragment so that it dies when the fragment dies
        viewModel.gameExists.observe(this, androidx.lifecycle.Observer(endObserver))

        viewModel.getGameUpdates().observe(this, androidx.lifecycle.Observer {
            //somone has reset game
            if(!it.started && navController.currentDestination?.id == R.id.gameFragment){
                viewModel.resetGame().addOnCompleteListener{
                   navController.popBackStack(R.id.waitingFragment, false)
                }
            }
        })
        //we can garuntee there will be no two users with the same super name
        if(viewModel.gameExists.value!!){
            currentPlayer = (viewModel.gameObject.value!!.playerObjectList).filter { it.username == viewModel.currentUser }[0]
        }

        timer = startTimer(viewModel.gameObject.value!!.timeLimit)

        tv_game_location.text = if(currentPlayer.role.toLowerCase().trim() == "the spy!"){
            "Figure out the location!"
        } else {
            "Location: ${viewModel.gameObject.value?.chosenLocation}"
        }
        tv_game_role.text = "Role: ${currentPlayer.role}"


        btn_end_game.setOnClickListener {endGame()}

        btn_play_again.setOnClickListener{
            viewModel.resetGame().addOnCompleteListener{_ ->
                navController.popBackStack(R.id.waitingFragment, false)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    navigateBack?.invoke()
                }
            })

        configurePlayersAdapter()
        configureLocationsAdapter()

        viewModel.getAllLocations().observe(this, androidx.lifecycle.Observer { locations ->
            locationsAdapter.items = locations
        })

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
        timer.cancel()
        navController.popBackStack(R.id.startFragment, false)
        activity!!.supportFragmentManager.beginTransaction().remove(this)
        Log.d("got here","got here")
    }

    private fun configureLocationsAdapter(){
        locationsAdapter = GameViewsAdapter(context!!, ArrayList())
        rv_locations.apply{
            adapter = locationsAdapter
            layoutManager = GridLayoutManager(context, 2) }
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
