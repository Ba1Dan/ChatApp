package com.baiganov.fintech.model

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stream(
    @SerialName("stream_id") val id: Int,
    @SerialName("name") val name: String,

) {
    var topics: List<Topic> = emptyList()
    var isSubscribed: Boolean = false
}