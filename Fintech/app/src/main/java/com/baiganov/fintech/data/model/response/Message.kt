package com.baiganov.fintech.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("content") val content: String,
    @SerialName("id")  val id: Int,
    @SerialName("reactions") var reactions: MutableList<Reaction>,
    @SerialName("sender_email") val senderEmail: String,
    @SerialName("sender_full_name") val senderFullName: String,
    @SerialName("sender_id") val senderId: Int,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("stream_id") val streamId: Int,
    @SerialName("subject") val topicName: String
)