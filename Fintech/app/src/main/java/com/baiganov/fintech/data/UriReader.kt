package com.baiganov.fintech.data

import android.net.Uri

interface UriReader {

    fun readBytes(uri: Uri): ByteArray?
}