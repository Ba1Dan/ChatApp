package com.baiganov.fintech.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TopicsResponse(
    @SerialName("topics") val topics: List<Topic>
)







