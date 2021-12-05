package com.baiganov.fintech.data

import android.content.Context
import android.net.Uri

class UriReaderImpl(private val context: Context) : UriReader {

    override fun readBytes(uri: Uri): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.readBytes()
    }
}