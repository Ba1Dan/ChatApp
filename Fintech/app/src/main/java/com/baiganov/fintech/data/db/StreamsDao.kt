package com.baiganov.fintech.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface StreamsDao {

    @Query("SELECT * FROM streams_table ORDER BY stream_id")
    fun getAllStreams(): Flowable<List<StreamEntity>>

    @Query("SELECT * FROM streams_table WHERE name LIKE :query ORDER BY stream_id")
    fun getStreams(query: String): Flowable<List<StreamEntity>>

    @Query("SELECT * FROM streams_table WHERE is_subscribed AND name LIKE :query ORDER BY stream_id")
    fun searchSubscribedStreams(query: String): Flowable<List<StreamEntity>>

    @Query("SELECT * FROM streams_table WHERE is_subscribed AND stream_id = :streamId ORDER BY stream_id")
    fun getSubscribedStreams(streamId: Int): List<StreamEntity>

    @Query("SELECT * FROM streams_table WHERE is_subscribed ORDER BY stream_id")
    fun getSubscribedStreamsDep(): Flowable<List<StreamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStreams(streams: List<StreamEntity>): Completable
}