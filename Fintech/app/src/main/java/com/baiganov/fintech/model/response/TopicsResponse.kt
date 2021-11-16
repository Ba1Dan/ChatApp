package com.baiganov.fintech.model.response

import com.baiganov.fintech.model.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TopicsResponse(
    @SerialName("topics") val topics: List<Topic>
)







