package com.baiganov.fintech.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TopicsResponse(
    @SerialName("topics") val topics: List<Topic>
)