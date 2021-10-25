package com.baiganov.fintech.model

data class Reaction(
    val userId: Int,
    val emoji: String,
    val count: Int
)