package com.baiganov.fintech.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FileResponse(
    @SerialName("uri") val uri: String
)
