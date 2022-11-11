package com.dangerfield.spyfall

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.util.isLegacyBuild
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isLegacyBuild()) {
            setContentView(R.layout.activity_main_legacy)
        } else {
            setContentView(R.layout.activity_main)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment).navigateUp()
    }
}