package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.model.AllStreamsResponse
import com.baiganov.fintech.data.model.SubscribedStreamsResponse
import com.baiganov.fintech.data.model.Subscription
import com.baiganov.fintech.data.model.TopicsResponse
import com.baiganov.fintech.data.network.ChatApi
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import javax.inject.Inject

interface ChannelsRemoteDataSource  {

    fun getAllStreams(): Single<AllStreamsResponse>
    fun getSubscribedStreams(): Single<SubscribedStreamsResponse>
    fun getTopics(streamId: Int): Single<TopicsResponse>
    fun createStream(subscription: Subscription): Completable

    class Base @Inject constructor(
        private val service: ChatApi
    ) : ChannelsRemoteDataSource {
        override fun getAllStreams(): Single<AllStreamsResponse> {
            return service.getStreams()
        }

        override fun getSubscribedStreams(): Single<SubscribedStreamsResponse> {
            return service.getSubscribedStreams()
        }

        override fun getTopics(streamId: Int): Single<TopicsResponse> {
            return service.getTopics(streamId)
        }

        override fun createStream(subscription: Subscription): Completable {
            return service.subscribeOnStreams(Json.encodeToString(serializer(), listOf(subscription)))
        }
    }
}