package com.baiganov.fintech.data

import com.baiganov.fintech.model.response.AllStreamsResponse
import com.baiganov.fintech.model.response.SubscribedStreamsResponse
import com.baiganov.fintech.model.response.TopicsResponse
import com.baiganov.fintech.network.NetworkModule
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class StreamRepository {

    private val dataManager = DataManager()
    private val networkModule = NetworkModule()
    private val service = networkModule.create()

    fun getStreams(): Single<AllStreamsResponse> {
        return service.getStreams()
    }

    fun getSubscribedStreams(): Single<SubscribedStreamsResponse> {
        return service.getSubscribedStreams()
    }

    fun getTopics(streamId: Int): Single<TopicsResponse> {
        return service.getTopics(streamId)
    }

    fun loadSubscribedStreams(searchQuery: String): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.subscribedStreams as List<ItemFingerPrint> }
            .delay(1000L, TimeUnit.MILLISECONDS)
            .map { streams ->
            streams.filter {
                it is StreamFingerPrint &&
                it.stream.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    fun openStream(type: Int, position: Int, topics: List<TopicFingerPrint>): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.add(type, position, topics) }
    }

    fun closeStream(type: Int, topics: List<TopicFingerPrint>): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.remove(type, topics) }
    }
}