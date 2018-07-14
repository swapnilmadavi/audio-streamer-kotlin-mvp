package com.swapyx.audiostreamer.audiostreamer.data.audioserver.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionData(
        val score: Int,
        val length: Double,
        @Json(name = "num_frames") val frames: Long
)
