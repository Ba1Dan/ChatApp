package com.baiganov.fintech.data.network

import com.baiganov.fintech.data.model.response.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
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
        @Query("anchor") anchor: Long,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int = 0,
        @Query("narrow") narrow: String,
        @Query("apply_markdown") applyMarkdown: Boolean = true
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

    @PATCH("messages/{msgId}")
    fun editMessageTopic(
        @Path("msgId") id: Int,
        @Query("topic") newTopic: String
    ): Completable

    @POST("users/me/subscriptions")
    fun subscribeOnStreams(
        @Query("subscriptions") subscriptions: String,
    ): Completable

    @POST("mark_topic_as_read")
    fun markTopicAsRead(
        @Query("stream_id") streamId: Int,
        @Query("topic_name") topicTitle: String
    ): Completable

    @POST("mark_stream_as_read")
    fun markStreamAsRead(
        @Query("stream_id") streamId: Int,
    ): Completable

    @Multipart
    @POST("user_uploads")
    fun uploadFile(
        @Part file: MultipartBody.Part
    ): Single<FileResponse>

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

    @DELETE("messages/{msg_id}")
    fun deleteMessage(
        @Path("msg_id") id: Int
    ): Completable

    @PATCH("messages/{msgId}")
    fun editMessageText(
        @Path("msgId") id: Int,
        @Query("content") newText: String
    ): Completable
}