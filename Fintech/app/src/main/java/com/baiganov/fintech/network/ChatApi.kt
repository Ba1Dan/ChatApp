package com.baiganov.fintech.network

import com.baiganov.fintech.model.StreamsResponse
import com.baiganov.fintech.model.TopicsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApi {

    @GET("streams")
    fun getStreams(): Single<StreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Single<TopicsResponse>
}