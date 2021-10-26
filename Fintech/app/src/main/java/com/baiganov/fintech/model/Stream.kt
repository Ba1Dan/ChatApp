package com.baiganov.fintech.model

data class Stream(
    val id: Int,
    val name: String,
    val topics: List<Topic>,
    var isSubscribed: Boolean = false
)