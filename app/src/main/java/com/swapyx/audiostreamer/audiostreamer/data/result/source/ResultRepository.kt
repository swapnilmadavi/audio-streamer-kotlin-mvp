package com.swapyx.audiostreamer.audiostreamer.data.result.source

import com.swapyx.audiostreamer.audiostreamer.data.result.source.remote.ResultRemoteDataSource

class ResultRepository(
        val resultRemoteDataSource: ResultRemoteDataSource
) : ResultDataSource {
    override fun loadSessionResult(sId: String) {
        if (sId != "") {
            resultRemoteDataSource.loadSessionResult(sId)
        }
    }
}