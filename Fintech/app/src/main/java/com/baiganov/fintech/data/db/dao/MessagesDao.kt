package com.baiganov.fintech.data.db.dao

import androidx.room.*
import com.baiganov.fintech.data.db.entity.MessageEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages_table WHERE topic_name = :topicName AND stream_id = :streamId")
    fun getTopicMessages(topicName: String, streamId: Int): Flowable<List<MessageEntity>>

    @Query("SELECT * FROM messages_table WHERE stream_id = :streamId")
    fun getStreamMessages(streamId: Int): Flowable<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMessages(messages: List<MessageEntity>): Completable

    @Query("DELETE FROM messages_table WHERE topic_name = :topicName AND stream_id = :streamId")
    fun deleteTopicMessages(topicName: String, streamId: Int): Completable

    @Query("DELETE FROM messages_table WHERE stream_id = :streamId")
    fun deleteStreamMessages(streamId: Int): Completable

    @Update
    fun updateMessage(messages: List<MessageEntity>): Completable
}