package com.baiganov.fintech.data.repository

import android.net.Uri
import com.baiganov.fintech.data.UriReader
import com.baiganov.fintech.data.datasource.MessageLocalDataSource
import com.baiganov.fintech.data.datasource.MessageRemoteDataSource
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.data.model.response.FileResponse
import com.baiganov.fintech.data.model.response.Message
import com.baiganov.fintech.domain.repository.MessageRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val uriReader: UriReader
) : MessageRepository {

    override fun loadMessages(
        streamTitle: String,
        topicTitle: String,
        anchor: Long,
        streamId: Int,
        numBefore: Int
    ): Completable {

        return messageRemoteDataSource.loadMessages(streamTitle, topicTitle, anchor,numBefore)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val messages = mapToEntity(it.messages)

                messageLocalDataSource.deleteTopicMessages(topicTitle, streamId)
                    .andThen(messageLocalDataSource.saveMessages(messages))
            }
    }

    override fun updateMessage(
        streamTitle: String,
        topicTitle: String,
        anchor: Long,
        numBefore: Int
    ): Completable {

        return messageRemoteDataSource.loadMessages(streamTitle, topicTitle, anchor,numBefore)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val messages = mapToEntity(it.messages)

                messageLocalDataSource.saveMessages(messages)
            }
    }

    override fun loadNextMessages(
        streamTitle: String,
        topicTitle: String,
        anchor: Long,
        numBefore: Int
    ): Completable {

        return messageRemoteDataSource.loadMessages(streamTitle, topicTitle, anchor,numBefore)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                val messages = mapToEntity(it.messages)

                messageLocalDataSource.saveMessages(messages)
            }
    }

    override fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable {
        return messageRemoteDataSource.sendMessage(streamId = streamId, message = message, topicTitle = topicTitle)
    }

    override fun addReaction(messageId: Int, emojiName: String): Completable {
        return messageRemoteDataSource.addReaction(messageId, emojiName)
    }

    override fun deleteReaction(messageId: Int, emojiName: String): Completable {
        return messageRemoteDataSource.deleteReaction(messageId, emojiName)
    }

    override fun deleteMessage(messageId: Int): Completable {
        return messageRemoteDataSource.deleteMessage(messageId)
    }

    override fun getMessagesFromDb(
        topicTitle: String,
        streamId: Int
    ): Flowable<List<MessageEntity>> =
        messageLocalDataSource.getTopicMessages(topicTitle, streamId)

    override fun uploadFile(uri: Uri, type: String, name: String): Single<FileResponse> {
        val bytes = uriReader.readBytes(uri)

        bytes?.let {
            val body = bytes.toRequestBody(type.toMediaType())
            val part = MultipartBody.Part.createFormData("file", name, body)


            return messageRemoteDataSource.uploadFile(part)
        }
        return Single.just(null)
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