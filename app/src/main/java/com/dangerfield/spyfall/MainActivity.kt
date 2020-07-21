package com.dangerfield.spyfall

import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.util.ThemeChangeableActivity

class MainActivity : ThemeChangeableActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findNavController(this, R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            val boy = when(destination.id) {
                R.id.startFragment -> "Start"
                R.id.newGameFragment -> "New Game"
                R.id.waitingFragment -> "Waiting"
                R.id.gameFragment -> "Game"
                R.id.settingsFragment -> "Setting"
                R.id.testerSettingsFragment -> "Tester setting"
                else -> "unknown"
            }

            Log.d("Elijah", "Destination changed: $boy")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment).navigateUp()
    }
}