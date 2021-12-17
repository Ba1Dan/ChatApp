package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.model.response.FileResponse
import com.baiganov.fintech.data.model.response.MessagesResponse
import com.baiganov.fintech.data.model.response.Narrow
import com.baiganov.fintech.data.network.ChatApi
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.MultipartBody
import javax.inject.Inject

class MessageRemoteDataSource @Inject constructor(
    private val service: ChatApi
) {

    fun loadMessages(
        stream: String,
        topicName: String?,
        anchor: Long,
        numBefore: Int,
    ): Single<MessagesResponse> {
        val narrow = getNarrow(stream, topicName)
        return service.getMessages(narrow = narrow, anchor = anchor, numBefore = numBefore)
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

    fun deleteMessage(messageId: Int): Completable {
        return service.deleteMessage(messageId)
    }

    fun uploadFile(part: MultipartBody.Part): Single<FileResponse> {
        return service.uploadFile(part)
    }

    fun markTopicAsRead(streamId: Int, topicTitle: String): Completable =
        service.markTopicAsRead(streamId, topicTitle)

    fun markStreamAsRead(streamId: Int): Completable =
        service.markStreamAsRead(streamId)

    fun editMessage(messageId: Int, content: String): Completable {
        return service.editMessageText(messageId, content)
    }

    fun editTopic(messageId: Int, newTopic: String): Completable {
        return service.editMessageTopic(messageId, newTopic)
    }

    private fun getNarrow(stream: String, topic: String?): String {
        return Json.encodeToString(
            serializer(),
            topic?.let {
                listOf(
                    Narrow(OPERATOR_STREAM, stream),
                    Narrow(OPERATOR_TOPIC, topic)
                )
            } ?: listOf(
                Narrow(OPERATOR_STREAM, stream),
            )
        )
    }

    companion object {
        private const val OPERATOR_STREAM = "stream"
        private const val OPERATOR_TOPIC = "topic"
    }
}