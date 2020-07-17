package com.dangerfield.spyfall

import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.util.ThemeChangeableActivity

class MainActivity : ThemeChangeableActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment).navigateUp()
    }
}