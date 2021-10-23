package com.baiganov.fintech.model

data class Content(
    val id: Int,
    val userId: Int,
    val name: String,
    val text: String,
    var reactions: MutableList<Reaction>
)