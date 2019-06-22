package com.dangerfield.spyfall

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.util.Receiver
import com.dangerfield.spyfall.game.GameViewModel

class MainActivity : AppCompatActivity(){

    lateinit var viewModel: GameViewModel
    lateinit var receiver: Receiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment).navigateUp()
    }

    override fun onResume() {
        super.onResume()
        register()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Main","on destory")

        //TODO: you might want to delete the game if the activity is destroyed, just know it will delete the game for everyone
        viewModel.endGame()
    }

    private fun register(){
        receiver = Receiver(viewModel)
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(receiver,intentFilter)
    }

}