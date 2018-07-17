package com.swapyx.audiostreamer.audiostreamer.data.audioserver.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SessionData(
        val score: Int,
        val length: Double,
        @Json(name = "num_frames") val frames: Long
) : Parcelable