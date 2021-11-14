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
}