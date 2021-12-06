package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.model.response.AllStreamsResponse
import com.baiganov.fintech.data.model.response.SubscribedStreamsResponse
import com.baiganov.fintech.data.model.response.TopicsResponse
import com.baiganov.fintech.data.network.ChatApi
import io.reactivex.Single
import javax.inject.Inject

class ChannelsRemoteDataSource @Inject constructor(
    private val service: ChatApi
) {

    fun getAllStreams(): Single<AllStreamsResponse> {
        return service.getStreams()
    }

    fun getSubscribedStreams(): Single<SubscribedStreamsResponse> {
        return service.getSubscribedStreams()
    }

    fun getTopics(streamId: Int): Single<TopicsResponse> {
        return service.getTopics(streamId)
    }
}