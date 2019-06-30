package com.dangerfield.spyfall

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.util.Receiver
import com.dangerfield.spyfall.game.GameViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope{

    lateinit var viewModel: GameViewModel
    lateinit var receiver: Receiver
    private  lateinit var killGame: Job
    private lateinit var navController: NavController
    private var killed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        killGame = Job()

        MobileAds.initialize(this, getString(R.string.ads_mod_app_id))

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        navController = findNavController(this,R.id.nav_host_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment).navigateUp()
    }

    override fun onResume() {
        super.onResume()
        register()
    }

    override val coroutineContext: CoroutineContext
        get() = killGame + Dispatchers.Main

    override fun onStart() {
        super.onStart()
        Log.d("Main","main activities on start was called")
        if(killGame.isActive){
            try{
                //if the user comes back before the 15 min timeout, cancel the job
                killGame.cancel()
            }catch (e: CancellationException){
                Log.d("Main","Killing of the game was cancelled")
            }
        }

        if(killed){
            navController.popBackStack(R.id.startFragment,false)
            killed = false
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Main","onDestroy Called, removing user")

        if(viewModel.gameExists.value != null && viewModel.gameExists.value!!){
            viewModel.removePlayer()
        }
    }

    private fun register(){
        receiver = Receiver(viewModel)
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(receiver,intentFilter)
    }

    override fun onStop() {
        super.onStop()
          if(viewModel.gameExists.value != null && viewModel.gameExists.value!!){

                //if the user closes the app after starting a game, wait 15 mins and remove the player
                killGame = GlobalScope.launch{
                    delay(900000)
                    killed = true
                    Log.d("Main","timeout finished, removing user")
                    viewModel.removePlayer()
                }
        }
    }
}