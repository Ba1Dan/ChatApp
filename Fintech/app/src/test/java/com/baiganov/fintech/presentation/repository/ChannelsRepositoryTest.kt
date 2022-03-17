package com.baiganov.fintech.presentation.repository

import com.baiganov.fintech.data.datasource.ChannelsLocalDataSource
import com.baiganov.fintech.data.datasource.ChannelsRemoteDataSource
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.data.model.AllStreamsResponse
import com.baiganov.fintech.data.model.SubscribedStreamsResponse
import com.baiganov.fintech.data.model.Subscription
import com.baiganov.fintech.data.model.TopicsResponse
import com.baiganov.fintech.data.repository.ChannelsRepositoryImpl
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Test

class ChannelsRepositoryTest {

    @Test
    fun `search streams`() {
        val channelsRemoteDataSource = ChannelsRemoteDataSourceTest()
        val channelsLocalDataSource = ChannelsLocalDataSourceTest()
        val repository = ChannelsRepositoryImpl(channelsRemoteDataSource, channelsLocalDataSource)

        repository.searchStreams("", 1).test().assertValue { list -> list.isNotEmpty() }

        repository.searchStreams("", 0).test().assertValue { list -> list.isEmpty() }
    }

    inner class ChannelsLocalDataSourceTest : ChannelsLocalDataSource {
        override fun getSubscribedStreams(streamId: Int): List<StreamEntity> {
            return listOf()
        }

        override fun saveStreams(streams: List<StreamEntity>): Completable {
            return Completable.complete()
        }

        override fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>> {
            return Flowable.just(listOf())
        }

        override fun searchAllStreams(searchQuery: String): Flowable<List<StreamEntity>> {
            return Flowable.just(listOf(StreamEntity(0, "", listOf(), false)))
        }
    }

    inner class ChannelsRemoteDataSourceTest : ChannelsRemoteDataSource {

        override fun getAllStreams(): Single<AllStreamsResponse> {
            return Single.just(AllStreamsResponse(listOf()))
        }

        override fun getSubscribedStreams(): Single<SubscribedStreamsResponse> {
            return Single.just(SubscribedStreamsResponse(listOf()))
        }

        override fun getTopics(streamId: Int): Single<TopicsResponse> {
            return Single.just(TopicsResponse(listOf()))
        }

        override fun createStream(subscription: Subscription): Completable {
            return Completable.complete()
        }
    }
}