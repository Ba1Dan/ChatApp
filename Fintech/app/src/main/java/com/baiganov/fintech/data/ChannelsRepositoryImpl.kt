package com.baiganov.fintech.data

import com.baiganov.fintech.data.db.StreamsDao
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.domain.repositories.ChannelsRepository
import com.baiganov.fintech.model.response.TopicsResponse
import com.baiganov.fintech.presentation.ui.channels.ChannelsPages
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChannelsRepositoryImpl @Inject constructor(
    private val service: ChatApi,
    private val streamsDao: StreamsDao
) : ChannelsRepository {

    override fun getAllStreams(): Completable {
        return service.getStreams()
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

    override fun getSubscribedStreams(): Completable {
        return service.getSubscribedStreams()
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

    override fun searchStreams(searchQuery: String, type: Int?): Flowable<List<StreamEntity>> =
        when (type) {
            ChannelsPages.SUBSCRIBED.ordinal -> streamsDao.searchSubscribedStreams("$searchQuery%")
            ChannelsPages.ALL_STREAMS.ordinal -> streamsDao.getStreams("$searchQuery%")
            else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $type")
        }


    override fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>> {
        return streamsDao.searchSubscribedStreams("$searchQuery%")
    }

    private fun getTopics(streamId: Int): Single<TopicsResponse> {
        return service.getTopics(streamId)
    }

    private fun getSubscribedStreamsFromDb(streamId: Int): List<StreamEntity> {
        return streamsDao.getSubscribedStreams(streamId)
    }


    private fun saveStreams(streams: List<StreamEntity>): Completable =
        streamsDao.saveStreams(streams)
}