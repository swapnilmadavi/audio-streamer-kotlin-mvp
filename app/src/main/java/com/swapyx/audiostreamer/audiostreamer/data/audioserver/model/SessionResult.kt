package com.swapyx.audiostreamer.audiostreamer.data.audioserver.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SessionResult(
        @Json(name = "sid") val sId: String,
        val processed: Boolean,
        val timestamp: Long,
        val data: SessionData
) : Parcelable