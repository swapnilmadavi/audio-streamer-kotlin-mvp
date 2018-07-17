package com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.remote

import com.swapyx.audiostreamer.audiostreamer.data.RemoteClientProvider
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface AudioService {

    @GET("session/{sId}")
    fun getResult(@Path("sId") sId: String): Call<SessionResult>

    companion object Factory {
        fun create(): AudioService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.1.101:8000/")
                    .client(RemoteClientProvider.provideRemoteClient())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            return retrofit.create(AudioService::class.java)
        }
    }
}