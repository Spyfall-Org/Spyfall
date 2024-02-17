package com.dangerfield.libraries.navigation.internal

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.dangerfield.oddoneoout.libraries.navigation.internal.R
import oddoneout.core.Try
import oddoneout.core.getOrElse
import oddoneout.core.logOnError

fun Context.openWebLinkFromContext(uri: Uri): Boolean =
    Try {
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
        .logOnError()
        .getOrElse { false }


fun Context.openWebLinkExternally(uri: Uri): Boolean {
    return Try {
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