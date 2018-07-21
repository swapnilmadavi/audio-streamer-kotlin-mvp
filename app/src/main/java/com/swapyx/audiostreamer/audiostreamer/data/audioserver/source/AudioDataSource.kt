package com.swapyx.audiostreamer.audiostreamer.data.audioserver.source

import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

interface AudioDataSource{
    interface FailureListener {
        fun onFailure()
    }

    interface LoadSessionResultListener : FailureListener {
        fun onSessionResultLoaded(result: SessionResult)
    }

    interface LoadPastSessionsListener : FailureListener {
        fun onPastSessionsLoaded(sessionList: List<Session>)
    }

    fun loadSessionResult(sId: String, listener: LoadSessionResultListener)

    fun loadPastSessions(listener: LoadPastSessionsListener)
}