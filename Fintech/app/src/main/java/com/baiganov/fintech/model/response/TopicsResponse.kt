package com.baiganov.fintech.model.response

import com.baiganov.fintech.model.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TopicsResponse(
    @SerialName("topics") val topics: List<Topic>
)

@Serializable
class MessagesResponse(
    @SerialName("messages") val messages: List<Message>,
    @SerialName("found_oldest") val foundOldest: Boolean
)

@Serializable
data class Message(
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("content") val content: String,
    @SerialName("id")  val id: Int,
    @SerialName("reactions") var reactions: MutableList<Reaction>,
    @SerialName("sender_email") val senderEmail: String,
    @SerialName("sender_full_name") val senderFullName: String,
    @SerialName("sender_id") val senderId: Int,
    @SerialName("timestamp") val timestamp: Int,
    @SerialName("stream_id") val streamId: Int,
    @SerialName("subject") val topicName: String
)

@Serializable
data class Reaction(
    @SerialName("emoji_code") val emojiCode: String,
    @SerialName("emoji_name") val emojiName: String,
    @SerialName("user_id") val userId: Int,
)

@Serializable
class Narrow(
    @SerialName("operator") val operator: String,
    @SerialName("operand") val operand: String
)