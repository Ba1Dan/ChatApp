package com.baiganov.fintech.data.model.response

import com.baiganov.fintech.data.model.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TopicsResponse(
    @SerialName("topics") val topics: List<Topic>
)







