package com.dangerfield.spyfall

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.legacy.util.isLegacyBuild
import com.dangerfield.spyfall.welcome.WelcomeNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var welcomeNavigator: WelcomeNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isLegacyBuild()) {
            setContentView(R.layout.activity_main_legacy)
        } else {
            setContentView(R.layout.activity_main)
            welcomeNavigator.navigateToSplash()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (isLegacyBuild()) {
            findNavController(this, R.id.nav_host_fragment).navigateUp()
        } else {
            super.onSupportNavigateUp()
        }
    }
}
