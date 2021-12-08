package com.baiganov.fintech.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id") val userId: Int,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("email") val email: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("is_active") val isActive: Boolean,
)