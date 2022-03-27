package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.db.dao.MessagesDao
import com.baiganov.fintech.data.db.entity.MessageEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

interface MessageLocalDataSource {

    fun saveMessages(messages: List<MessageEntity>): Completable
    fun deleteTopicMessages(topicTitle: String, streamId: Int): Completable
    fun deleteStreamMessages(streamId: Int): Completable
    fun getStreamMessages(streamId: Int): Flowable<List<MessageEntity>>
    fun getTopicMessages(topicTitle: String, streamId: Int): Flowable<List<MessageEntity>>

    class Base @Inject constructor(
        private val messagesDao: MessagesDao
    ) : MessageLocalDataSource {

        override fun saveMessages(messages: List<MessageEntity>): Completable {
            return messagesDao.saveMessages(messages)
        }

        override fun deleteTopicMessages(
            topicTitle: String,
            streamId: Int,
        ): Completable {
            return messagesDao.deleteTopicMessages(topicTitle, streamId)
        }

        override fun deleteStreamMessages(streamId: Int): Completable {
            return messagesDao.deleteStreamMessages(streamId)
        }

        override fun getStreamMessages(streamId: Int): Flowable<List<MessageEntity>> {
            return messagesDao.getStreamMessages(streamId)
        }

        override fun getTopicMessages(
            topicTitle: String,
            streamId: Int
        ): Flowable<List<MessageEntity>> {
            return messagesDao.getTopicMessages(topicTitle, streamId)
        }
    }
}