package com.swapyx.audiostreamer.audiostreamer.data.audioserver.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionResult(
        @Json(name = "sid") val sId: String,
        val processed: Boolean,
        val timestamp: Long,
        val data: SessionData
)
