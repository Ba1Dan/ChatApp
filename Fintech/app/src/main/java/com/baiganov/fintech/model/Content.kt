package com.baiganov.fintech.model

class Content(
    val id: Int,
    val userId: Int,
    val name: String,
    val text: String,
    var reactions: MutableList<Reaction>
) : Item