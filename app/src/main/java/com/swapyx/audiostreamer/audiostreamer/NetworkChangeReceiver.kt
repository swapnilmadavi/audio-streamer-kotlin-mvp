package com.swapyx.audiostreamer.audiostreamer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.swapyx.audiostreamer.audiostreamer.util.NetworkUtils

class NetworkChangeReceiver(private val listener: NetworkChangeListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val connected = NetworkUtils.isNetworkConnected(context)

        listener.onNetworkStatusChanged(connected)
    }

    interface NetworkChangeListener {
        fun onNetworkStatusChanged(connected: Boolean)
    }
}
