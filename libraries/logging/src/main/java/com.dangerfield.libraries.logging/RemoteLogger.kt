package com.dangerfield.libraries.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class RemoteLogger(private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()) : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = true

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val p = when (priority) {
            Log.DEBUG -> "Debug"
            Log.INFO -> "Info"
            Log.WARN -> "Warn"
            Log.ERROR -> "Error"
            else -> "Unknown Priority"
        }

        crashlytics.log("priority: $p ${tag?.let { "| $it"}}: \n$message")

        t?.let { crashlytics.recordException(it) }
    }
}
