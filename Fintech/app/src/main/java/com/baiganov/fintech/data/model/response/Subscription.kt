package com.baiganov.fintech.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Subscription(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String
)
