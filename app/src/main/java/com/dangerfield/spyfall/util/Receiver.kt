package com.dangerfield.spyfall.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.dangerfield.spyfall.game.GameViewModel

class Receiver(var viewModel: GameViewModel): BroadcastReceiver() {

    //this receiver listens for internet changes, and notifies the viewModel

    override fun onReceive(context: Context?, intent: Intent?) {

        val networkInfo: NetworkInfo? = (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

        viewModel.hasNetworkConnection.value = networkInfo != null && networkInfo.isConnected

    }

}