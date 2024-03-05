package oddoneout.core

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle

fun Context.findActivity(): Activity =
    requireNotNull(findActivityOrNull()) {
        "No activity associated with this context"
    }

fun Context.findActivityOrNull(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivityOrNull()
        else -> null
    }

@Suppress("SwallowedException")
fun Context.openStoreLinkToApp(buildInfo: BuildInfo) {
    try {
        val uri = Uri.parse("market://details?id=" + buildInfo.playStorePackageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(goToMarket, Bundle())
    } catch (e: ActivityNotFoundException) {
        val uri = Uri.parse("http://play.google.com/store/apps/details?id=" + buildInfo.playStorePackageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(goToMarket, Bundle())
    }
}