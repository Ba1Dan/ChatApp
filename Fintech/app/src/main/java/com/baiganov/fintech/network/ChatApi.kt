package com.baiganov.fintech.network

import com.baiganov.fintech.model.response.*
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface ChatApi {

    @GET("streams")
    fun getStreams(): Single<AllStreamsResponse>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedStreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Single<TopicsResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: Long = 10000000000000000,
        @Query("num_before") numBefore: Int = 100,
        @Query("num_after") numAfter: Int = 0,
        @Query("narrow") narrow: String,
        @Query("apply_markdown") applyMarkdown: Boolean = false
    ): Single<MessagesResponse>

    @GET("users")
    fun getUsers(): Single<UsersResponse>

    @GET("users/me")
    fun getOwnUser(): Single<User>

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