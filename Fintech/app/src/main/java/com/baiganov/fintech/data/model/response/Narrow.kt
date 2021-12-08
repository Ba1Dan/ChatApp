package com.baiganov.fintech.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Narrow(
    @SerialName("operator") val operator: String,
    @SerialName("operand") val operand: String
)