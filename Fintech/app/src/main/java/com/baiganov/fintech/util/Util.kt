package com.baiganov.fintech.util

import java.text.SimpleDateFormat
import java.util.*

private const val FORMAT_DAY = "d MMM"
private const val FORMAT_MINUTES = "HH:mm"

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat(FORMAT_MINUTES, Locale.getDefault())
    val date = Date(timestamp * 1000L)

    return sdf.format(date)
}

fun formatDateByDay(timestamp: Long): String {
    val sdf = SimpleDateFormat(FORMAT_DAY, Locale.getDefault())
    val date = Date(timestamp * 1000L)

    return sdf.format(date)
}