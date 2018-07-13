package com.swapyx.audiostreamer.audiostreamer.data.result.source.remote

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.swapyx.audiostreamer.audiostreamer.data.result.source.ResultDataSource
import okhttp3.*
import java.io.IOException

class ResultRemoteDataSource private constructor(
        private val remoteClient: OkHttpClient
) : ResultDataSource {

    private val TAG = ResultRemoteDataSource::class.java.simpleName

    override fun loadSessionResult(sId: String, listener: ResultDataSource.LoadSessionListener) {
        val handler = Handler(Looper.getMainLooper())

        remoteClient.newCall(getResultRequest(sId)).enqueue(object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d(TAG, "onFailure => ${e?.localizedMessage}")
                handler.post {
                    listener.onFailure()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                var responseString : String? = null
                try {
                    responseString = response?.body()?.string()
                } catch (e: Exception){
                    Log.d(TAG, "onResponse error=> ${e.localizedMessage}")
                }

                handler.post {
                    if (responseString != null) {
                        listener.onSessionResultLoaded(responseString.toString())
                    } else {
                        listener.onSessionResultLoaded("Error")
                    }
                }
            }
        })
    }

    private fun getResultRequest(sId: String) : Request {
        val url = "http://192.168.1.100:8000/session/$sId"

        return Request.Builder()
                .url(url)
                .build()
    }

    companion object {
        private var INSTANCE: ResultRemoteDataSource? = null

        @JvmStatic
        fun getInstance(remoteClient: OkHttpClient): ResultRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(ResultRemoteDataSource::javaClass) {
                    INSTANCE = ResultRemoteDataSource(remoteClient)
                }
            }
            return INSTANCE!!
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }

}