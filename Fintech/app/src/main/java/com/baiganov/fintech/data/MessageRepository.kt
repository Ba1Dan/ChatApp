package com.baiganov.fintech.data

import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class MessageRepository {

    private val dataManager = DataManager()

    fun loadMessages(): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.messages as List<ItemFingerPrint> }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }

    fun addMessage(message: String): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.addMessage(message) }
    }

    fun addEmoji(position: Int, emoji: String): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.addEmoji(position, emoji) }
    }
}