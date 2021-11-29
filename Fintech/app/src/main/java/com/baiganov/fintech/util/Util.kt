package com.baiganov.fintech.util

import java.text.SimpleDateFormat
import java.util.*


fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(timestamp * 1000L)

    return sdf.format(date)
}

fun formatDateByDay(timestamp: Long): String {
    val sdf = SimpleDateFormat("d MMM", Locale.getDefault())
    val date = Date(timestamp * 1000L)

    return sdf.format(date)
}