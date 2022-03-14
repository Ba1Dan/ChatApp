package com.baiganov.fintech.presentation.util

import android.util.Log
import androidx.core.text.HtmlCompat
import com.baiganov.fintech.di.NetworkModule
import org.jsoup.Jsoup
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

fun parseHtml(content: String): CharSequence {
    val doc = Jsoup.parse(content, NetworkModule.BASE_URL)
    doc.select("a")?.forEach { e ->
        Log.d("rpogjrtbngjuirthbyu", "abs ${e.absUrl("href")}")
        e.attr("href", e.absUrl("href"))
    }

    doc.getElementsByTag("img")?.remove()

    return HtmlCompat.fromHtml(doc.html(), HtmlCompat.FROM_HTML_MODE_COMPACT).trim()
}