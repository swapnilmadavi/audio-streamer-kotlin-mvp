package com.swapyx.audiostreamer.audiostreamer.data.audioserver.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Session(
        val sid: String,
        val timestamp: Long,
        val processed: Boolean
)