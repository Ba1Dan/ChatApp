package com.baiganov.fintech.data

import com.baiganov.fintech.data.db.StreamsDao
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.model.response.TopicsResponse
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class StreamRepository(
    private val service: ChatApi,
    private val streamsDao: StreamsDao
) {

    fun getAllStreams(): Completable {
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
                        isSubscribed = false
                    )
                }
                saveStreams(entities)
            }
    }

    fun getSubscribedStreams(): Completable {
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

    fun searchStreams(searchQuery: String): Flowable<List<StreamEntity>> {
        return streamsDao.getStreams("$searchQuery%")
    }

    fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>> {
        return streamsDao.searchSubscribedStreams("$searchQuery%")
    }

    private fun getTopics(streamId: Int): Single<TopicsResponse> {
        return service.getTopics(streamId)
    }

    private fun getSubscribedStreamsFromDb(): Single<List<StreamEntity>> =
        streamsDao.getSubscribedStreams()

    private fun saveStreams(streams: List<StreamEntity>): Completable =
        streamsDao.saveStreams(streams)
}