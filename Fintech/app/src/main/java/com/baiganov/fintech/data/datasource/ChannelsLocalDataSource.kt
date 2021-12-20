package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.db.dao.StreamsDao
import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class ChannelsLocalDataSource @Inject constructor(private val streamsDao: StreamsDao) {

    fun getSubscribedStreams(streamId: Int): List<StreamEntity> {
        return streamsDao.getSubscribedStreams(streamId)
    }

    fun saveStreams(streams: List<StreamEntity>): Completable =
        streamsDao.saveStreams(streams)

    fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>> {
        return streamsDao.searchSubscribedStreams(searchQuery)
    }

    fun searchAllStreams(searchQuery: String): Flowable<List<StreamEntity>> {
        return streamsDao.searchAllStreams(searchQuery)
    }
}