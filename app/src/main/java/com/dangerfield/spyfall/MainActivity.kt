package com.dangerfield.spyfall

import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.util.ThemeChangeableActivity
import com.dangerfield.spyfall.util.isLegacyBuild
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

class MainActivity : ThemeChangeableActivity(), AndroidScopeComponent{

    override val scope: Scope by activityScope()

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