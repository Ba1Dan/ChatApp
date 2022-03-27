package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.model.FileResponse
import com.baiganov.fintech.data.model.MessagesResponse
import com.baiganov.fintech.data.model.Narrow
import com.baiganov.fintech.data.network.ChatApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.MultipartBody
import javax.inject.Inject

interface MessageRemoteDataSource {

    fun loadMessages(
        stream: String,
        topicName: String?,
        anchor: Long,
        numBefore: Int,
        numAfter: Int
    ): Single<MessagesResponse>

    fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable

    fun addReaction(messageId: Int, emojiName: String): Completable

    fun deleteReaction(messageId: Int, emojiName: String): Completable

    fun deleteMessage(messageId: Int): Completable

    fun editMessage(messageId: Int, content: String): Completable

    fun uploadFile(part: MultipartBody.Part): Single<FileResponse>

    fun markTopicAsRead(streamId: Int, topicTitle: String): Completable

    fun markStreamAsRead(streamId: Int): Completable

    fun editTopic(messageId: Int, newTopic: String): Completable

    class Base @Inject constructor(
        private val service: ChatApi
    ) : MessageRemoteDataSource {

        override fun loadMessages(
            stream: String,
            topicName: String?,
            anchor: Long,
            numBefore: Int,
            numAfter: Int
        ): Single<MessagesResponse> {
            val narrow = getNarrow(stream, topicName)
            return service.getMessages(
                narrow = narrow,
                anchor = anchor,
                numBefore = numBefore,
                numAfter = numAfter
            )
        }

        override fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable {
            return service.sendMessage(streamId = streamId, text = message, topicTitle = topicTitle)
        }

        override fun addReaction(messageId: Int, emojiName: String): Completable {
            return service.addReaction(messageId, emojiName)
        }

        override fun deleteReaction(messageId: Int, emojiName: String): Completable {
            return service.deleteReaction(messageId, emojiName)
        }

        override fun deleteMessage(messageId: Int): Completable {
            return service.deleteMessage(messageId)
        }

        override fun uploadFile(part: MultipartBody.Part): Single<FileResponse> {
            return service.uploadFile(part)
        }

        override fun markTopicAsRead(streamId: Int, topicTitle: String): Completable =
            service.markTopicAsRead(streamId, topicTitle)

        override fun markStreamAsRead(streamId: Int): Completable =
            service.markStreamAsRead(streamId)

        override fun editMessage(messageId: Int, content: String): Completable {
            return service.editMessageText(messageId, content)
        }

        override fun editTopic(messageId: Int, newTopic: String): Completable {
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
}