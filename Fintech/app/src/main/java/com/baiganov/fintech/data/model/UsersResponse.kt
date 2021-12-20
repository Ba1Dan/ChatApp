package com.baiganov.fintech.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UsersResponse(
    @SerialName("members") val users: List<User>,
)