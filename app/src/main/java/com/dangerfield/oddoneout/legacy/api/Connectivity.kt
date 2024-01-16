package com.dangerfield.oddoneout.legacy.api

import com.dangerfield.oddoneout.legacy.util.ConnectivityHelper
import java.io.IOException

class Connectivity : ConnectivityHelper {

    /**
     * Checks whether device is currently online or not by Pinging a server
     * This uses Google's DNS (very unlikely to ever be down)
     * Preferable compared to connectivity/session manager as connected != online
     */

    override suspend fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}
