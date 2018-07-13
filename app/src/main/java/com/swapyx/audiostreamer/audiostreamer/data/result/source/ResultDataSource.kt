package com.swapyx.audiostreamer.audiostreamer.data.result.source

interface ResultDataSource {

    interface LoadSessionListener {
        fun onSessionResultLoaded(resultJson: String)
        fun onFailure()
    }

    fun loadSessionResult(sId: String, listener: LoadSessionListener)
}