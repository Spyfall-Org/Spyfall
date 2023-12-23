package com.dangerfield.libraries.navigation.internal

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.dangerfield.spyfall.libraries.navigation.internal.R
import spyfallx.core.Try
import spyfallx.core.getOrElse

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
        .getOrElse { false }