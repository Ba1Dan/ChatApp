package com.baiganov.fintech.data.repository

import android.net.Uri
import com.baiganov.fintech.data.UriReader
import com.baiganov.fintech.data.db.MessagesDao
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.domain.repository.MessageRepository
import com.baiganov.fintech.data.model.response.FileResponse
import com.baiganov.fintech.data.model.response.Message
import com.baiganov.fintech.data.model.response.Narrow
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val service: ChatApi,
    private val messagesDao: MessagesDao,
    private val uriReader: UriReader
) : MessageRepository {

    override fun loadMessages(
        stream: String,
        topicName: String,
        anchor: Long,
        streamId: Int,
        numBefore: Int
    ): Completable {
        val narrow = getNarrow(stream, topicName)

        return service.getMessages(narrow = narrow, anchor = anchor, numBefore = numBefore)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val messages = mapToEntity(it.messages)

                messagesDao.deleteTopicMessages(topicName, streamId)
                    .andThen(messagesDao.saveMessages(messages))
            }
    }

    override fun updateMessage(
        stream: String,
        topic: String,
        anchor: Long,
        numBefore: Int
    ): Completable {
        val narrow = getNarrow(stream, topic)

        return service.getMessages(narrow = narrow, anchor = anchor, numBefore = numBefore)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val messages = mapToEntity(it.messages)

                messagesDao.saveMessages(messages)
            }
    }

    override fun loadNextMessages(
        stream: String,
        topic: String,
        anchor: Long,
        numBefore: Int
    ): Completable {
        val narrow = getNarrow(stream, topic)

        return service.getMessages(narrow = narrow, anchor = anchor, numBefore = numBefore)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val messages = mapToEntity(it.messages)

                messagesDao.saveMessages(messages)
            }
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

    override fun getMessagesFromDb(
        topicName: String,
        streamId: Int
    ): Flowable<List<MessageEntity>> =
        messagesDao.getTopicMessages(topicName, streamId)

    override fun uploadFile(uri: Uri, type: String, name: String): Single<FileResponse> {
        val bytes = uriReader.readBytes(uri)

        bytes?.let {
            val body = bytes.toRequestBody(type.toMediaType())
            val part = MultipartBody.Part.createFormData("file", name, body)


            return service.uploadFile(part)
        }
        return Single.just(null)
    }

    private fun getNarrow(stream: String, topic: String): String {
        return Json.encodeToString(
            listOf(
                Narrow(OPERATOR_STREAM, stream),
                Narrow(OPERATOR_TOPIC, topic)
            )
        )
    }

    private fun mapToEntity(list: List<Message>): List<MessageEntity> {
        return list.map { message ->
            MessageEntity(
                id = message.id,
                avatarUrl = message.avatarUrl,
                content = message.content,
                reactions = message.reactions,
                senderEmail = message.senderEmail,
                senderFullName = message.senderFullName,
                senderId = message.senderId,
                timestamp = message.timestamp,
                streamId = message.streamId,
                topicName = message.topicName,
            )
        }
    }

    companion object {
        private const val OPERATOR_STREAM = "stream"
        private const val OPERATOR_TOPIC = "topic"
    }
}