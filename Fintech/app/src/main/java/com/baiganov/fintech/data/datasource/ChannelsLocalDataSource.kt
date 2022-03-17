package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.db.dao.StreamsDao
import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

interface ChannelsLocalDataSource {

    fun getSubscribedStreams(streamId: Int): List<StreamEntity>
    fun saveStreams(streams: List<StreamEntity>): Completable
    fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>>
    fun searchAllStreams(searchQuery: String): Flowable<List<StreamEntity>>

    class Base @Inject constructor(private val streamsDao: StreamsDao) : ChannelsLocalDataSource{

        override fun getSubscribedStreams(streamId: Int): List<StreamEntity> {
            return streamsDao.getSubscribedStreams(streamId)
        }

        override fun saveStreams(streams: List<StreamEntity>): Completable =
            streamsDao.saveStreams(streams)

        override fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>> {
            return streamsDao.searchSubscribedStreams(searchQuery)
        }

        override fun searchAllStreams(searchQuery: String): Flowable<List<StreamEntity>> {
            return streamsDao.searchAllStreams(searchQuery)
        }
    }
}