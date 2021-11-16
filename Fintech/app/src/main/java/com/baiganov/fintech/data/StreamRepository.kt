package com.baiganov.fintech.data

import android.content.Context
import com.baiganov.fintech.data.db.DatabaseModule
import com.baiganov.fintech.data.db.StreamsDao
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.model.response.AllStreamsResponse
import com.baiganov.fintech.model.response.SubscribedStreamsResponse
import com.baiganov.fintech.model.response.TopicsResponse
import com.baiganov.fintech.data.network.NetworkModule
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow

class StreamRepository(
    private val context: Context,
) {

    private val networkModule = NetworkModule()
    private val service = networkModule.create()

    private val databaseModule = DatabaseModule()
    private val streamsDao: StreamsDao = databaseModule.create(context).streamsDao()

    fun getStreams(): Completable {
        return service.getStreams()
            .subscribeOn(Schedulers.io())
            .flattenAsObservable { streamResponse ->
                streamResponse.streams
            }
            .flatMapSingle { stream ->
                getTopics(stream.id)
                    .zipWith(Single.just(stream)) { topicsResponse, _ ->
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

    fun searchSubscribedStreams(): Single<SubscribedStreamsResponse> {
        return service.getSubscribedStreams()
    }

    fun searchStreams(): Single<AllStreamsResponse> {
        return service.getStreams()
    }



    fun getSubscribedStreams(): Completable {
        return service.getSubscribedStreams().subscribeOn(Schedulers.io())
            .flattenAsObservable { streamResponse ->
                streamResponse.streams
            }
            .flatMapSingle { stream ->
                getTopics(stream.id)
                    .zipWith(Single.just(stream)) { topicsResponse, _ ->
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

    fun getTopics(streamId: Int): Single<TopicsResponse> {
        return service.getTopics(streamId)
    }

    fun saveStreams(streams: List<StreamEntity>): Completable = streamsDao.saveStreams(streams)

    fun getStreamsFromDb(): Flowable<List<StreamEntity>> = streamsDao.getStreams()

    fun getSubscribedStreamsFromDb(): Flowable<List<StreamEntity>> = streamsDao.getSubscribedStreams()
}