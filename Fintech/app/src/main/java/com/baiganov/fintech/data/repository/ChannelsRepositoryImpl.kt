package com.baiganov.fintech.data.repository

import com.baiganov.fintech.data.datasource.ChannelsLocalDataSource
import com.baiganov.fintech.data.datasource.ChannelsRemoteDataSource
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.data.model.Subscription
import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.data.model.TopicsResponse
import com.baiganov.fintech.presentation.ui.channels.ChannelsPages
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChannelsRepositoryImpl @Inject constructor(
    private val remoteDataSource: ChannelsRemoteDataSource,
    private val localDataSource: ChannelsLocalDataSource
) : ChannelsRepository {

    override fun getStreams(type: Int): Completable {
        return getStreamsByTabPosition(type)
    }

    override fun searchStreams(searchQuery: String, type: Int?): Flowable<List<StreamEntity>> =
        when (type) {
            ChannelsPages.SUBSCRIBED.ordinal -> localDataSource.searchSubscribedStreams("$searchQuery%")
            ChannelsPages.ALL_STREAMS.ordinal -> localDataSource.searchAllStreams("$searchQuery%")
            else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $type")
        }

    override fun createStream(name: String, description: String): Completable {
        return remoteDataSource.createStream(Subscription(name, description))
    }

    private fun getTopics(streamId: Int): Single<TopicsResponse> {
        return remoteDataSource.getTopics(streamId)
    }

    private fun getSubscribedStreamsFromDb(streamId: Int): List<StreamEntity> {
        return localDataSource.getSubscribedStreams(streamId)
    }

    private fun saveStreams(streams: List<StreamEntity>): Completable =
        localDataSource.saveStreams(streams)

    private fun getStreamsByTabPosition(type: Int): Completable = when (type) {
        ChannelsPages.SUBSCRIBED.ordinal -> getSubscribedStreams()
        ChannelsPages.ALL_STREAMS.ordinal -> getAllStreams()
        else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $type")
    }

    private fun getAllStreams(): Completable {
        return remoteDataSource.getAllStreams()
            .subscribeOn(Schedulers.io())
            .flattenAsObservable { streamResponse ->
                streamResponse.streams
            }
            .flatMapSingle { stream ->
                getTopics(stream.id)
                    .map { topicsResponse ->
                        stream.apply {
                            topics = topicsResponse.topics
                            isSubscribed = getSubscribedStreamsFromDb(stream.id).isNotEmpty()
                        }
                    }
            }
            .toList()
            .flatMapCompletable { streams ->
                val entities = streams.map { stream ->
                    StreamEntity(
                        streamId = stream.id,
                        name = stream.name,
                        topics = stream.topics,
                        isSubscribed = stream.isSubscribed
                    )
                }
                saveStreams(entities)
            }
    }

    private fun getSubscribedStreams(): Completable {
        return remoteDataSource.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flattenAsObservable { streamResponse ->
                streamResponse.streams
            }
            .flatMapSingle { stream ->
                getTopics(stream.id)
                    .map { topicsResponse ->
                        stream.apply {
                            topics = topicsResponse.topics
                        }
                    }
            }
            .toList()
            .flatMapCompletable { streams ->
                val entities = streams.map { stream ->
                    StreamEntity(
                        streamId = stream.id,
                        name = stream.name,
                        topics = stream.topics,
                        isSubscribed = true
                    )
                }
                saveStreams(entities)
            }
    }
}