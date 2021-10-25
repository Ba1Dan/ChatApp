package com.baiganov.fintech.model

import com.baiganov.fintech.recyclerview.Item

data class Content(
    val id: Int,
    val userId: Int,
    val name: String,
    val text: String,
    var reactions: MutableList<Reaction>
)