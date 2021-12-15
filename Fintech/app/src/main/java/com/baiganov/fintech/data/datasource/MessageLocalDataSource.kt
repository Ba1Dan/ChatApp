package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.db.MessagesDao
import com.baiganov.fintech.data.db.entity.MessageEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class MessageLocalDataSource @Inject constructor(
    private val messagesDao: MessagesDao
) {

    fun saveMessages(messages: List<MessageEntity>): Completable {
        return messagesDao.saveMessages(messages)
    }

    fun deleteTopicMessages(
        topicTitle: String,
        streamId: Int,
    ): Completable {
        return messagesDao.deleteTopicMessages(topicTitle, streamId)
    }

    fun deleteStreamMessages(
        streamId: Int,
    ): Completable {
        return messagesDao.deleteStreamMessages(streamId)
    }

    fun getStreamMessages(
        streamId: Int
    ): Flowable<List<MessageEntity>> {
        return messagesDao.getStreamMessages(streamId)
    }

    fun getTopicMessages(
        topicTitle: String,
        streamId: Int
    ): Flowable<List<MessageEntity>> {
        return messagesDao.getTopicMessages(topicTitle, streamId)
    }
}