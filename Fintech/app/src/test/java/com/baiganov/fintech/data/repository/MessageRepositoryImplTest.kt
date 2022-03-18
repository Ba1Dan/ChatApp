package com.baiganov.fintech.data.repository

import android.net.Uri
import com.baiganov.fintech.data.UriReader
import com.baiganov.fintech.data.datasource.MessageLocalDataSource
import com.baiganov.fintech.data.datasource.MessageRemoteDataSource
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.data.model.FileResponse
import com.baiganov.fintech.data.model.Message
import com.baiganov.fintech.data.model.MessagesResponse
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MultipartBody
import org.junit.Before
import org.junit.Test

class MessageRepositoryImplTest {

    private lateinit var repository: MessageRepositoryImpl

    @Before
    fun setup() {
        repository = MessageRepositoryImpl(
            MessageRemoteDataSourceTest(),
            MessageLocalDataSourceTest(),
            UriReaderTest()
        )
    }

    @Test
    fun `get messages`() {
        repository.loadMessages("", "", 0L, 0, 0, 0).subscribe()
        repository.getTopicMessages("1", 0).test().assertValue { messages -> messages.isNotEmpty() }
        repository.getStreamMessages(1).test().assertValue { messages -> messages.isEmpty() }
    }

    @Test
    fun `edit messages`() {
        repository.loadMessages("", "", 0L, 0, 0, 0).subscribe()

        repository.editMessage(0, "new content").subscribe()
        repository.loadMessages("", "", 0L, 0, 0, 0).subscribe()
        repository.getTopicMessages("1", 0).test()
            .assertValue { messages -> messages.filter { it.id == 0 }[0].content == "new content" }
    }

    inner class MessageLocalDataSourceTest : MessageLocalDataSource {

        private val data = mutableListOf<MessageEntity>()

        override fun saveMessages(messages: List<MessageEntity>): Completable {
            data.addAll(messages)
            return Completable.complete()
        }

        override fun deleteTopicMessages(topicTitle: String, streamId: Int): Completable {
            data.filter { messageEntity -> messageEntity.topicName != topicTitle && messageEntity.streamId != streamId }
            return Completable.complete()
        }

        override fun deleteStreamMessages(streamId: Int): Completable {
            data.filter { messageEntity -> messageEntity.streamId != streamId }
            return Completable.complete()
        }

        override fun getStreamMessages(streamId: Int): Flowable<List<MessageEntity>> {
            return if (streamId == 0) {
                Flowable.just(data)
            } else {
                Flowable.just(listOf())
            }
        }

        override fun getTopicMessages(
            topicTitle: String,
            streamId: Int
        ): Flowable<List<MessageEntity>> {
            return Flowable.just(data)
        }
    }

    inner class MessageRemoteDataSourceTest : MessageRemoteDataSource {

        private val messages = mutableListOf(
            Message(
                "",
                "",
                0,
                mutableListOf(),
                "",
                "",
                0,
                0L,
                0,
                "1"
            )
        )

        override fun loadMessages(
            stream: String,
            topicName: String?,
            anchor: Long,
            numBefore: Int,
            numAfter: Int
        ): Single<MessagesResponse> {
            return Single.just(
                MessagesResponse(
                    messages, false
                )
            )
        }

        override fun sendMessage(streamId: Int, message: String, topicTitle: String): Completable {
            return Completable.complete()
        }

        override fun addReaction(messageId: Int, emojiName: String): Completable {
            return Completable.complete()
        }

        override fun deleteReaction(messageId: Int, emojiName: String): Completable {
            return Completable.complete()
        }

        override fun deleteMessage(messageId: Int): Completable {
            return Completable.complete()
        }

        override fun editMessage(messageId: Int, content: String): Completable {
            val message = messages.find { it.id == messageId }
            if (message != null) {
                messages.remove(message)
                val changedMessage = Message(
                    message.avatarUrl,
                    content,
                    message.id,
                    message.reactions,
                    message.senderEmail,
                    message.senderFullName,
                    message.senderId,
                    message.timestamp,
                    message.streamId,
                    message.topicName
                )
                messages.add(changedMessage)
            }
            return Completable.complete()
        }

        override fun uploadFile(part: MultipartBody.Part): Single<FileResponse> {
            TODO("Not yet implemented")
        }

        override fun markTopicAsRead(streamId: Int, topicTitle: String): Completable {
            return Completable.complete()
        }

        override fun markStreamAsRead(streamId: Int): Completable {
            return Completable.complete()
        }

        override fun editTopic(messageId: Int, newTopic: String): Completable {
            return Completable.complete()
        }
    }

    class UriReaderTest : UriReader {
        override fun readBytes(uri: Uri): ByteArray? {
            TODO("Not yet implemented")
        }
    }
}