package com.baiganov.fintech.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface StreamsDao {

    @Query("SELECT * FROM streams_table WHERE name LIKE :query ORDER BY stream_id")
    fun searchAllStreams(query: String): Flowable<List<StreamEntity>>

    @Query("SELECT * FROM streams_table WHERE is_subscribed AND name LIKE :query ORDER BY stream_id")
    fun searchSubscribedStreams(query: String): Flowable<List<StreamEntity>>

    @Query("SELECT * FROM streams_table WHERE is_subscribed AND stream_id = :streamId ORDER BY stream_id")
    fun getSubscribedStreams(streamId: Int): List<StreamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStreams(streams: List<StreamEntity>): Completable
}