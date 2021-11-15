package com.baiganov.fintech.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface StreamsDao {

    @Query("SELECT * FROM streams_table ORDER BY stream_id")
    fun getStreams(): Flowable<List<StreamEntity>>

    @Query("SELECT * FROM streams_table WHERE is_subscribed ORDER BY stream_id")
    fun getSubscribedStreams(): Flowable<List<StreamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStreams(streams: List<StreamEntity>): Completable
}