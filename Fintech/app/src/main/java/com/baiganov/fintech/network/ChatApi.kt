package com.baiganov.fintech.network

import com.baiganov.fintech.model.StreamsResponse
import com.baiganov.fintech.model.response.MessagesResponse
import com.baiganov.fintech.model.response.Narrow
import com.baiganov.fintech.model.response.TopicsResponse
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.http.*

interface ChatApi {

    @GET("streams")
    fun getStreams(): Single<StreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Single<TopicsResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String = "first_unread",
        @Query("num_before") numBefore: Int = 50,
        @Query("num_after") numAfter: Int = 0,
        @Query("narrow") narrow: String,
        @Query("apply_markdown") applyMarkdown: Boolean = true
    ): Single<MessagesResponse>

    @POST("messages")
    fun sendMessage(
        @Query("type") type: String = "stream",
        @Query("to") streamId: Int,
        @Query("content") text: String,
        @Query("topic") topicTitle: String
    ): Completable

    @POST("messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String,
        @Query("reaction_type") reactionType: String = "unicode_emoji",
    ): Completable

    @DELETE("messages/{message_id}/reactions")
    fun deleteReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Completable
}