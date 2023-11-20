package com.dangerfield.libraries.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class RemoteLogger(private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()) : Timber.Tree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.DEBUG

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val priorityName = when (priority) {
            Log.DEBUG -> "Debug"
            Log.INFO -> "Info"
            Log.WARN -> "Warn"
            Log.ERROR -> "Error"
            else -> null
        }

        if (priorityName != null) {
            crashlytics.log("$priorityName|$tag: $message")
        }

        if (t != null) {
            crashlytics.recordException(t)
        }
    }
}
