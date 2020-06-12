package com.dangerfield.spyfall.game

import android.content.res.Configuration
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.models.Player
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import com.google.android.gms.ads.AdRequest

class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var currentPlayer: Player
    private lateinit var locationsAdapter: GameViewsAdapter
    private lateinit var playersAdapter: GameViewsAdapter
    private var changingTheme = false
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //this does not get called again, set all the variables you want to keep(not reassign)
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)

        viewModel.incrementAndroidPlayers()

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    //show alert when user presses back
                    UIHelper.customSimpleAlert(context!!,
                        getString(R.string.leave_game_title),
                        getString(R.string.leave_in_game_message),
                        getString(R.string.leave_action_positive),
                        {endGame()},getString(R.string.leave_action_negative),{}).show()
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tv_game_role.maxTextSize = 96.0f

        if(BuildConfig.FLAVOR == "free") adView2.loadAd(AdRequest.Builder().build()) else adView2.visibility = View.GONE


        //the observers should not be called unless the fragment is in a started or resumed state
        viewModel.getGameUpdates().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            //someone has reset game
            if(!it.started && navController.currentDestination?.id == R.id.gameFragment){
                navController.popBackStack(R.id.waitingFragment, false)
            }
            //now this gets called anytime anything changes
            if(it.started && navController.currentDestination?.id == R.id.gameFragment){
                //but right now it says: if the game has started, and im still on this screen, and something has changed..
                configurePlayerViews()
                configurePlayersAdapter(viewModel.gameObject.value!!.playerObjectList[0].username)
                configureLocationsAdapter()
            }
        })

        viewModel.gameExists.observe(viewLifecycleOwner, androidx.lifecycle.Observer { exists ->
            //someone ended the game
            if(!exists && navController.currentDestination?.id == R.id.gameFragment){ endGame() }
        })

        viewModel.getTimeLeft().observe(viewLifecycleOwner, androidx.lifecycle.Observer {time ->
            tv_game_timer.text = time
            btn_play_again.visibility = if(time == "0:00") View.VISIBLE else View.GONE
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeAccent()
        //we set the listeners once the view has actually been inflated
        btn_end_game.setOnClickListener {
            if(tv_game_timer.text.toString() == "0:00") endGame()
            else{
                UIHelper.customSimpleAlert(context!!,
                    getString(R.string.end_game_title),
                    getString(R.string.end_game_message),
                    getString(R.string.end_game_positive_action), {endGame()},
                    getString(R.string.negative_action_standard),{}).show()
            }
        }

        btn_play_again.setOnClickListener{
            viewModel.resetGame()
        }
        btn_hide.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btn_hide.setOnClickListener{ hide()}

        tv_game_timer.text = String.format(
            Locale.getDefault(), "%d:%02d",
            viewModel.gameObject.value?.timeLimit, 0
        )
    }

    override fun onResume() {
        super.onResume()
        //just to be super safe
        if(viewModel.gameTimer == null){
            Crashlytics.log("Game time was null in on resume, resetting")
            viewModel.startGameTimer()
        }

        Log.d("Elijah", "Resumeing with game: ${viewModel.gameObject.value}")

        if(viewModel.gameObject.value?.started == false && navController.currentDestination?.id == R.id.gameFragment) {
            //then user returned to the game but the game has been reset
            navController.popBackStack(R.id.waitingFragment, false)
        }
    }

    private fun hide(){
        if(tv_game_role.visibility == View.VISIBLE){
            tv_game_role.visibility = View.GONE
            tv_game_location.visibility = View.GONE
            view_role_card.visibility = View.GONE
            btn_hide.text = resources.getString(R.string.string_show)
        }else{
            tv_game_role.visibility = View.VISIBLE
            view_role_card.visibility = View.VISIBLE
            tv_game_location.visibility = View.VISIBLE
            btn_hide.text = resources.getString(R.string.string_hide)
        }
    }


    fun endGame(){
        viewModel.endGame()
        navController.popBackStack(R.id.startFragment, false)
    }

    private fun configureLocationsAdapter(){
        locationsAdapter = GameViewsAdapter(context!!, ArrayList(), null)
        rv_locations.apply{
            adapter = locationsAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        locationsAdapter.items = viewModel.gameObject.value!!.locationList
    }

    private fun configurePlayersAdapter(firstPlayer: String){
        rv_players.apply{
            layoutManager = GridLayoutManager(context, 2)
            playersAdapter = GameViewsAdapter(context,(viewModel.gameObject.value!!.playerList.shuffled()) as ArrayList<String>, firstPlayer)
            adapter = playersAdapter
            setHasFixedSize(true)
        }
    }

    private fun configurePlayerViews() {
        //we enforce that no two users have the same username
        val filteredPlayerList = (viewModel.gameObject.value!!.playerObjectList).filter { it.username == viewModel.currentUser }
        if(filteredPlayerList.isNotEmpty()){
            currentPlayer = filteredPlayerList[0]
        }else{
            Crashlytics.log("could not find player \"${viewModel.currentUser}\" in player object list for game: ${viewModel.gameObject.value}")
            navController.popBackStack(R.id.waitingFragment, false)
            Toast.makeText(context, "Something went wrong please check all players internet connection and try again", Toast.LENGTH_LONG).show()
        }

        //dont let the spy know the location
        tv_game_location.text = if(currentPlayer.role.toLowerCase().trim() == "the spy!"){
            "Figure out the location!"
        } else { "Location: ${viewModel.gameObject.value?.chosenLocation}" }

        tv_game_role.text = "Role: ${currentPlayer.role}"

    }

    private fun changeAccent(){
        btn_end_game.background.setTint(UIHelper.accentColor)
        btn_hide.background.setTint(UIHelper.accentColor)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changingTheme = true
    }

    override fun onDestroy() {
        super.onDestroy()

        if(!changingTheme) {
            viewModel.gameTimer?.cancel()
            viewModel.gameTimer = null
        }
        changingTheme = false
    }
}
