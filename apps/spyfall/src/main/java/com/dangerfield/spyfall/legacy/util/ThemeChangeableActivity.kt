package com.dangerfield.spyfall.legacy.util

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class ThemeChangeableActivity : AppCompatActivity() {
    private var themeChanged = false
    private var uiTheme = Configuration.UI_MODE_NIGHT_YES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentMode == Configuration.UI_MODE_NIGHT_NO) {
            uiTheme = Configuration.UI_MODE_NIGHT_NO
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val updatedTheme = (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK)
        if (uiTheme != updatedTheme) {
            this.themeChanged = true
        }
        this.recreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        themeChanged = false
    }
}
