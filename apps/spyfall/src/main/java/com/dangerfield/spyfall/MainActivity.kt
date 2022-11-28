package com.dangerfield.spyfall

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.legacy.ui.settings.RequireUpdateFragment
import com.dangerfield.spyfall.legacy.util.CheckForForcedUpdate
import com.dangerfield.spyfall.legacy.util.UIHelper
import com.dangerfield.spyfall.legacy.util.isLegacyBuild
import com.dangerfield.spyfall.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main_legacy.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val checkForForcedUpdate: CheckForForcedUpdate by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isLegacyBuild()) {
            setContentView(R.layout.activity_main_legacy)
        } else {
            setContentView(R.layout.activity_main)
            supportFragmentManager.commit { add(R.id.content, SplashFragment()) }
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Default).launch {
            if (checkForForcedUpdate.shouldRequireUpdate()) {
                if (isLegacyBuild()) {
                    showBlockingUpdateLegacy()
                } else {
                    showBlockingUpdate()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (isLegacyBuild()) {
            findNavController(this, R.id.nav_host_fragment).navigateUp()
        } else {
            super.onSupportNavigateUp()
        }
    }

    private fun showBlockingUpdate() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val backStackRootId = supportFragmentManager.getBackStackEntryAt(0).id
            supportFragmentManager.popBackStack(backStackRootId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        supportFragmentManager.commit {
            replace(R.id.content, RequireUpdateFragment())
        }
    }

    private fun showBlockingUpdateLegacy() {
        val navController = findNavController(this, R.id.nav_host_fragment)
        if (navController.currentDestination?.id != R.id.requireUpdateFragment) {
            navController.navigate(R.id.action_startFragment_to_requireUpdateFragment)
        }
    }
}
