package com.baiganov.fintech.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    @SerialName("max_id") val id: Int,
    @SerialName("name") val title: String
)