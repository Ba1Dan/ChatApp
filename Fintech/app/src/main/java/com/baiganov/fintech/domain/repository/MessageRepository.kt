package com.baiganov.fintech.domain.repository

import android.net.Uri
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.data.model.response.FileResponse
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface MessageRepository {

    fun loadMessages(stream: String, topicName: String, anchor: Long, streamId: Int, numBefore: Int): Completable

    fun updateMessage(
        stream: String,
        topic: String,
        anchor: Long,
        numBefore: Int
    ): Completable

    fun loadNextMessages(stream: String, topic: String, anchor: Long, numBefore: Int): Completable

    fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable

    fun addReaction(messageId: Int, emojiName: String): Completable

    fun deleteReaction(messageId: Int, emojiName: String): Completable

    fun deleteMessage(messageId: Int): Completable

    fun getMessagesFromDb(topicName: String, streamId: Int): Flowable<List<MessageEntity>>

    fun uploadFile(uri: Uri, type: String, name: String): Single<FileResponse>
}