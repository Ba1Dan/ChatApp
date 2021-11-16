package com.baiganov.fintech.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baiganov.fintech.data.db.entity.MessageEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages_table WHERE topic_name = :topicName AND stream_id = :streamId")
    fun getTopicMessages(topicName: String, streamId: Int): Flowable<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMessages(messages: List<MessageEntity>): Completable

    @Query("DELETE FROM messages_table WHERE topic_name = :topicName AND stream_id = :streamId")
    fun deleteTopicMessages(topicName: String, streamId: Int): Completable
}