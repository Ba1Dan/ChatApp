package com.baiganov.fintech.domain.repositories

import com.baiganov.fintech.data.db.entity.MessageEntity
import io.reactivex.Completable
import io.reactivex.Flowable

interface MessageRepository {

    fun loadMessages(stream: String, topicName: String, anchor: Long, streamId: Int): Completable

    fun updateMessage(
        stream: String,
        topic: String,
        anchor: Long,
        numBefore: Int
    ): Completable

    fun loadNextMessages(stream: String, topic: String, anchor: Long): Completable

    fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable

    fun addReaction(messageId: Int, emojiName: String): Completable

    fun deleteReaction(messageId: Int, emojiName: String): Completable

    fun deleteMessage(messageId: Int): Completable

    fun getMessagesFromDb(topicName: String, streamId: Int): Flowable<List<MessageEntity>>
}