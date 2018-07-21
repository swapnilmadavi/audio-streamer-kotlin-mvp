package com.swapyx.audiostreamer.audiostreamer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * An utility class to check Network connection status.
 */
object NetworkUtils {

    /**
     * Checks if the network is connected
     */
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }
}// This utility class is not publicly instantiable