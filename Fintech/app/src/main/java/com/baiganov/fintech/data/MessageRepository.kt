package com.baiganov.fintech.data

import com.baiganov.fintech.model.response.MessagesResponse
import com.baiganov.fintech.model.response.Narrow
import com.baiganov.fintech.network.NetworkModule
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class MessageRepository {

    private val networkModule = NetworkModule()
    private val service = networkModule.create()

    fun loadMessages(stream: String, topic: String): Single<MessagesResponse> {
        val narrow = Json.encodeToString(listOf(Narrow(OPERATOR_STREAM, stream), Narrow(OPERATOR_TOPIC, topic)))
        return service.getMessages(narrow = narrow)
    }

    fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable {
        return service.sendMessage(streamId = streamId, text = message, topicTitle = topicTitle)
    }

    fun addReaction(messageId: Int, emojiName: String): Completable {
        return service.addReaction(messageId, emojiName)
    }

    fun deleteReaction(messageId: Int, emojiName: String): Completable {
        return service.deleteReaction(messageId, emojiName)
    }

    companion object {
        private const val OPERATOR_STREAM = "stream"
        private const val OPERATOR_TOPIC = "topic"
    }
}