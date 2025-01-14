package com.dangerfield.libraries.navigation.internal

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.dangerfield.oddoneoout.libraries.navigation.internal.R
import oddoneout.core.Catching
import oddoneout.core.logOnFailure

fun Context.openWebLinkFromContext(uri: Uri): Boolean =
    Catching {
        val builder = CustomTabsIntent.Builder()

        if (resources != null) {
            builder.setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .build()
            )
        }

        builder.setStartAnimations(
            this,
            R.anim.translate_from_right,
            R.anim.translate_to_left
        )
        builder.setExitAnimations(
            this,
            R.anim.translate_from_left,
            R.anim.translate_to_right
        )

        val customTabsIntent = builder.build()

        customTabsIntent.launchUrl(this, uri)
        true
    }
        .logOnFailure()
        .getOrElse { false }


fun Context.openWebLinkExternally(uri: Uri): Boolean {
    return Catching {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            // Ensure that this intent can be handled
            if (resolveActivity(packageManager) != null) {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            } else {
                // No browser is installed, intent cannot be handled
                return false
            }
        }
        startActivity(intent)
        true
    }.getOrElse { false }
}