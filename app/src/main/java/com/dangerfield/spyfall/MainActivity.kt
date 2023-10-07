package com.dangerfield.spyfall

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.legacy.util.isLegacyBuild
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import spyfallx.core.BuildInfo
import com.dangerfield.spyfall.legacy.util.collectWhileStarted
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModel()

    @Inject
    lateinit var buildInfo: BuildInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (buildInfo.isLegacySpyfall) {
           legacyOnCreate()
        } else {
            refactorOnCreate()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun legacyOnCreate() {
        setContentView(R.layout.activity_main_legacy)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mainActivityViewModel.state.collectWhileStarted(this) { state ->
            if (state == MainActivityViewModel.State.UpdateRequired) {
                Log.d("Elijah", "showing splash")
                showBlockingUpdateLegacy()
            }
        }
    }

    private fun refactorOnCreate() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        setContent {
            SpyfallApp()
        }
    }

    /*
    so ill make a litte ext fun called rememberHiltInjected<T> that uses entry points
    to get the T and remember it

    ill just need to make a little annotation
    @ComposeInjectable that will generate the little entry point using kotlin poet
    ill make this its own little library on github called

    ill need to work for more than just class targets
    itll need to work for @provides fun targets as well. Ill get chat gpts help.
    
    Compose Hilt Injection
     */

    override fun onSupportNavigateUp(): Boolean {
        return if (isLegacyBuild()) {
            findNavController(this, R.id.nav_host_fragment).navigateUp()
        } else {
            super.onSupportNavigateUp()
        }
    }

    private fun showBlockingUpdateLegacy() {
        val navController = findNavController(this, R.id.nav_host_fragment)
        if (navController.currentDestination?.id != R.id.requireUpdateFragment) {
            navController.navigate(R.id.action_startFragment_to_requireUpdateFragment)
        }
    }
}
