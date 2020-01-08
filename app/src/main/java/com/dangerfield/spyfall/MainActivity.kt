package com.dangerfield.spyfall

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.game.GameViewModel
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope{

    lateinit var viewModel: GameViewModel
    private  lateinit var killGame: Job
    private lateinit var navController: NavController
    private var killed = false
    private var themeChanged = false
    private var uiTheme = Configuration.UI_MODE_NIGHT_YES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        killGame = Job()

        MobileAds.initialize(this, getString(R.string.ads_mod_app_id))

        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentMode == Configuration.UI_MODE_NIGHT_NO) {
            uiTheme = Configuration.UI_MODE_NIGHT_NO
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        navController = findNavController(this,R.id.nav_host_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment).navigateUp()
    }


    override val coroutineContext: CoroutineContext
        get() = killGame + Dispatchers.Main

    override fun onStart() {
        super.onStart()
        Log.d("Eli","main activities on start was called")
        if(killGame.isActive){
            try{
                //if the user comes back before the 15 min timeout, cancel the job
                killGame.cancel()
            }catch (e: CancellationException){
                Log.d("Eli","Killing of the game was cancelled")
            }
        }

        //if the user has come back after they have been removed, make sure they go back to the start
        if(killed){
            navController.popBackStack(R.id.startFragment,false)
            killed = false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        Log.d("Eli","on config change")
        if(uiTheme != (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK)){
            this.themeChanged = true
        }
        this.recreate()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Eli","onDestroy Called, removing user")

        if(viewModel.gameExists.value != null
            && viewModel.gameExists.value!!
            && !themeChanged){ //dont remove player if the activity was restared for theme change
            viewModel.removePlayer()
        }

        themeChanged = false
    }


    override fun onStop() {
        super.onStop()
          if(viewModel.gameExists.value != null && viewModel.gameExists.value!!){
                //if the user closes the app after starting a game, wait 15 mins and remove the player
                killGame = GlobalScope.launch{
                    delay(900000 )
                    killed = true
                    Log.d("Eli","timeout finished, removing user")
                    viewModel.removePlayer()
                }
        }
    }
}