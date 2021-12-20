package com.baiganov.fintech.data.model

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