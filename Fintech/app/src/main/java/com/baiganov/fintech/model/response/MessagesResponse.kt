package com.baiganov.fintech.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessagesResponse(
    @SerialName("messages") val messages: List<Message>,
    @SerialName("found_oldest") val foundOldest: Boolean
)