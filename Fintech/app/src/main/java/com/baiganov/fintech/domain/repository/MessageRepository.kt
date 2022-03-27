package com.baiganov.fintech.domain.repository

import android.net.Uri
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.data.model.FileResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface MessageRepository {

    fun loadMessages(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long,
        streamId: Int,
        numBefore: Int,
        numAfter: Int
    ): Completable

    fun updateMessage(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long,
        numBefore: Int,
        numAfter: Int
    ): Completable

    fun loadNextMessages(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long,
        numBefore: Int,
        numAfter: Int
    ): Completable

    fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable

    fun addReaction(messageId: Int, emojiName: String): Completable

    fun deleteReaction(messageId: Int, emojiName: String): Completable

    fun deleteMessage(messageId: Int): Completable

    fun getTopicMessages(topicTitle: String, streamId: Int): Flowable<List<MessageEntity>>

    fun getStreamMessages(streamId: Int): Flowable<List<MessageEntity>>

    fun uploadFile(uri: Uri, type: String, name: String): Single<FileResponse>

    fun editMessage(messageId: Int, content: String): Completable

    fun editTopic(messageId: Int, newTopic: String): Completable
}