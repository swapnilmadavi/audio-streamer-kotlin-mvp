package com.swapyx.audiostreamer.audiostreamer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast

fun Context.showToastMessage(message: String, duration: Int) {
    Toast.makeText(this.applicationContext, message, duration).show()
}