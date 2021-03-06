package com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.remote

import android.util.Log
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.AudioDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AudioRemoteDataSource : AudioDataSource {

    private val TAG = AudioRemoteDataSource::class.java.simpleName

    override fun loadPastSessions(listener: AudioDataSource.LoadPastSessionsListener) {
        val call = AudioService.create().getPastSessions()

        call.enqueue(object : Callback<List<Session>> {
            override fun onResponse(call: Call<List<Session>>?, response: Response<List<Session>>?) {
                val result = response?.body()
                if (result != null) {
                    listener.onPastSessionsLoaded(result)
                } else {
                    listener.onFailure()
                }
            }

            override fun onFailure(call: Call<List<Session>>?, t: Throwable?) {
                Log.d(TAG, "onFailure => ${t?.localizedMessage}")
                listener.onFailure()
            }

        })
    }

    override fun loadSessionResult(sId: String, listener: AudioDataSource.LoadSessionResultListener) {
        val call = AudioService.create().getResult(sId)

        call.enqueue(object : Callback<SessionResult> {
            override fun onResponse(call: Call<SessionResult>?, response: Response<SessionResult>?) {
                val result = response?.body()
                if (result != null) {
                    listener.onSessionResultLoaded(result)
                } else {
                    listener.onFailure()
                }
            }

            override fun onFailure(call: Call<SessionResult>?, t: Throwable?) {
                Log.d(TAG, "onFailure => ${t?.localizedMessage}")
                listener.onFailure()
            }
        })
    }
}