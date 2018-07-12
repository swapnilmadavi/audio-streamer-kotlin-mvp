package com.swapyx.audiostreamer.audiostreamer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import java.time.Duration

fun Context.isConnectedToInternet() : Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}

fun Context.showToastMessage(message: String, duration: Int) {
    Toast.makeText(this.applicationContext, message, duration).show()
}