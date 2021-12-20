package com.baiganov.fintech.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserPresenceResponse(
    @SerialName("presence") val presence: Presence,
)

@Serializable
class Presence(
    @SerialName("aggregated") val aggregated: PresenceClient,
)

@Serializable
class PresenceClient(
    @SerialName("status") val status: String,
    @SerialName("timestamp") val timestamp: Int
)