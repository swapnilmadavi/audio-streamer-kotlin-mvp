package com.swapyx.audiostreamer.audiostreamer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkChangeReceiver(private val listener: NetworkChangeListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val connected = getConnectivityStatus(context)

        listener.onNetworkStatusChanged(connected)
    }

    private fun getConnectivityStatus(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    interface NetworkChangeListener {
        fun onNetworkStatusChanged(connected: Boolean)
    }
}
