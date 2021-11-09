package com.baiganov.fintech.model.response

import com.baiganov.fintech.model.Stream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AllStreamsResponse(
    @SerialName("streams") val streams: List<Stream>
)

@Serializable
class SubscribedStreamsResponse(
    @SerialName("subscriptions") val streams: List<Stream>
)