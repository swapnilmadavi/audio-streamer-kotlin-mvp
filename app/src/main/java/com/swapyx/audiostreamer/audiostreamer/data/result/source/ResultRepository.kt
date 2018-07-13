package com.swapyx.audiostreamer.audiostreamer.data.result.source

import com.swapyx.audiostreamer.audiostreamer.data.result.source.remote.ResultRemoteDataSource

class ResultRepository(
        private val resultRemoteDataSource: ResultRemoteDataSource
) : ResultDataSource {
    override fun loadSessionResult(sId: String, listener: ResultDataSource.LoadSessionListener) {
        if (sId != "") {
            resultRemoteDataSource.loadSessionResult(sId, object: ResultDataSource.LoadSessionListener{
                override fun onSessionResultLoaded(resultJson: String) {
                    listener.onSessionResultLoaded(resultJson)
                }

                override fun onFailure() {
                    listener.onFailure()
                }

            })
        }
    }
}