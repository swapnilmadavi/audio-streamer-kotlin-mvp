package com.swapyx.audiostreamer.audiostreamer.data

import okhttp3.OkHttpClient

object RemoteClientProvider {
    fun provideRemoteClient() : OkHttpClient {
        return OkHttpClient()
    }
}