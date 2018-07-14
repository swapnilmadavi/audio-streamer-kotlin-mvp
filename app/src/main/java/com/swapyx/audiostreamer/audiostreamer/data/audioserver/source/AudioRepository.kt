package com.swapyx.audiostreamer.audiostreamer.data.audioserver.source

import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.remote.AudioRemoteDataSource

class AudioRepository private constructor(
        private val audioRemoteDataSource: AudioRemoteDataSource
) : AudioDataSource {

    override fun loadSessionResult(sId: String, listener: AudioDataSource.LoadSessionListener) {
        audioRemoteDataSource.loadSessionResult(sId, object: AudioDataSource.LoadSessionListener{
            override fun onSessionResultLoaded(result: SessionResult) {
                listener.onSessionResultLoaded(result)
            }

            override fun onFailure() {
                listener.onFailure()
            }

        })
    }

    companion object {

        private var INSTANCE: AudioRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic fun getInstance(audioRemoteDataSource: AudioRemoteDataSource): AudioRepository {
            return INSTANCE ?: AudioRepository(audioRemoteDataSource)
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }

}