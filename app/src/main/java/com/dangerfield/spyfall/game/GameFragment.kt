package com.dangerfield.spyfall.game


import android.os.Bundle
import android.os.CountDownTimer
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
import com.dangerfield.spyfall.R


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
        tv_game_location.text = if(currentPlayer.role.toLowerCase().trim() == "the spy!"){
            "Figure out the location!"
        } else {
            "Location: ${viewModel!!.gameObject.value?.chosenLocation}"
        }
        tv_game_role.text = "Role: ${currentPlayer.role}"


        btn_end_game.setOnClickListener {
            viewModel.endGame()
            timer.cancel()
            Navigation.findNavController(view).popBackStack(R.id.startFragment, false)
        }

        btn_play_again.setOnClickListener{
            viewModel.resetGame().addOnCompleteListener{
                Navigation.findNavController(view).popBackStack(R.id.waitingFragment, false)
            }
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
                showPlayAgain()
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


    fun showPlayAgain(){

        val padding = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.getDisplayMetrics()
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
}
