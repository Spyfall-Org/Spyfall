package com.dangerfield.spyfall

import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.util.ThemeChangeableActivity
import com.google.android.gms.ads.MobileAds

class MainActivity : ThemeChangeableActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this, getString(R.string.ads_mod_app_id))
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment).navigateUp()
    }
}