package com.swapyx.audiostreamer.audiostreamer.data.audioserver.source

import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

interface AudioDataSource{
    interface FailureListener {
        fun onFailure()
    }

    interface LoadSessionListener : FailureListener {
        fun onSessionResultLoaded(result: SessionResult)
    }

    fun loadSessionResult(sId: String, listener: LoadSessionListener)
}