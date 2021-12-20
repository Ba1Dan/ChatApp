package com.baiganov.fintech.presentation.ui.chat

import android.net.Uri
import android.util.Log
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.domain.repository.MessageRepository
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.ui.chat.recyclerview.DateDividerFingerPrint
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import com.baiganov.fintech.util.formatDateByDay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject
import kotlin.properties.Delegates

@InjectViewState
class ChatPresenter @Inject constructor(
    private val messageRepository: MessageRepository
) : MvpPresenter<ChatView>() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()
    private var topicTitle: String? = null
    private var isLoading = true

    var isConnected: Boolean = false

    fun init(streamTitle: String, streamId: Int, topicTitle: String?) {
        this.streamTitle = streamTitle
        this.topicTitle = topicTitle
        this.streamId = streamId
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun obtainEvent(event: Event.EventChat) {

        when (event) {
            is Event.EventChat.LoadFirstMessages -> {
                topicTitle?.let { getMessagesFromDb(it, streamId) } ?: getStreamMessages(streamId)

                loadMessage(
                    streamTitle,
                    topicTitle,
                    event.streamId
                )

            }

            is Event.EventChat.LoadNextMessages -> {
                if (isConnected) {
                    loadNextMessages(
                        streamTitle,
                        topicTitle,
                        event.anchor
                    )
                }
            }

            is Event.EventChat.AddReaction -> {
                if (isConnected) {
                    addReaction(
                        event.messageId,
                        event.emojiName
                    )
                }
            }

            is Event.EventChat.DeleteReaction -> {
                if (isConnected) {
                    deleteReaction(
                        event.messageId,
                        event.emojiName
                    )
                }
            }

            is Event.EventChat.SendMessage -> {
                if (isConnected) {
                    sendMessage(
                        event.streamId,
                        event.message,
                        event.topicTitle,
                    )
                }
            }

            is Event.EventChat.DeleteMessage -> {
                if (isConnected) {
                    deleteMessage(
                        event.streamId,
                        event.messageId
                    )
                }
            }

            is Event.EventChat.UploadFile -> {
                if (isConnected) {
                    uploadFile(event.uri, event.type, event.name)
                }
            }

            is Event.EventChat.EditMessage -> {
                if (isConnected) {
                    editMessage(event.messageId, event.content)
                }
            }

            is Event.EventChat.EditTopic -> {
                if (isConnected) {
                    editTopic(event.messageId, event.newTopic)
                }
            }

            else -> {
                Log.d(javaClass.simpleName, "unknown type event")
            }
        }
    }

    private fun uploadFile(uri: Uri, type: String, name: String) {
        messageRepository.uploadFile(uri, type, name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                viewState.render(State.LoadingFile)
            }
            .subscribe(
                { fileResponse ->
                    viewState.render(
                        State.AddFile(
                            "[${fileResponse.uri.split("/").last()}](${fileResponse.uri})"
                        )
                    )
                },
                {
                    viewState.render(State.Error(it.message))
                    Log.d("upload_message", "error = ${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun getMessagesFromDb(topicTitle: String, streamId: Int) {
        messageRepository.getTopicMessages(topicTitle, streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { streamEntities ->

                    val mess = streamEntities.groupBy { formatDateByDay(it.timestamp) }
                        .flatMap { (date: String, messagesByDate: List<MessageEntity>) ->
                            listOf(DateDividerFingerPrint(date)) +
                                    messagesByDate.map { message -> MessageFingerPrint(message) }
                        }

                    if (mess.isEmpty() && isLoading) {
                        viewState.render(State.Loading)
                    } else {
                        viewState.render(State.Result(mess))
                    }
                },
                onError = {
                    viewState.render(State.Error(it.message))
                    Log.d(javaClass.simpleName, "error get messages from db  ${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun getStreamMessages(streamId: Int) {
        messageRepository.getStreamMessages(streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { messages ->


                    if (messages.isEmpty() && isLoading) {
                        viewState.render(State.Loading)
                    } else {
                        val messagesFingerPrint = getMessagesByDivider(messages)
                        viewState.render(State.Result(messagesFingerPrint))
                    }
                },
                onError = {
                    Log.d(javaClass.simpleName, "error get messages from db  ${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun sendMessage(
        streamId: Int,
        message: String,
        _topicTitle: String
    ) {
        val topic = checkContent(_topicTitle)
        messageRepository.sendMessage(streamId, message, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topic)
                },
                onError = {
                    viewState.render(State.Error("error send message"))
                    Log.d(javaClass.simpleName, "${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loadMessage(
        streamTitle: String,
        topicTitle: String?,
        streamId: Int,
        anchor: Long = DEFAULT_ANCHOR,
        numAfter: Int = NUM_AFTER
    ) {
        messageRepository.loadMessages(
            streamTitle,
            topicTitle,
            anchor,
            streamId,
            INITIAL_PAGE_SIZE,
            numAfter
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                    isLoading = false
                },
                onError = { exception ->
                    viewState.render(State.Error(exception.message))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loadNextMessages(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long
    ) {
        messageRepository.loadNextMessages(
            streamTitle,
            topicTitle,
            anchor,
            INITIAL_PAGE_SIZE,
            NUM_AFTER
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                },
                onError = { exception ->
                    viewState.render(State.Error(exception.message))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun addReaction(
        messageId: Int,
        emojiName: String
    ) {
        messageRepository.addReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    Log.d(
                        javaClass.simpleName,
                        "error add reaction ${it.message}"
                    )
                }
            )
            .addTo(compositeDisposable)
    }

    private fun deleteReaction(
        messageId: Int,
        emojiName: String
    ) {
        messageRepository.deleteReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    Log.d(
                        javaClass.simpleName,
                        "error delete reaction ${it.message}"
                    )
                }
            )
            .addTo(compositeDisposable)
    }

    private fun deleteMessage(
        streamId: Int,
        messageId: Int,
    ) {
        messageRepository.deleteMessage(messageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    loadMessage(
                        streamTitle,
                        topicTitle,
                        streamId,
                        messageId.toLong(),
                        NUM_BEFORE_DELETE
                    )
                },
                onError = {
                    Log.d(
                        javaClass.simpleName,
                        "error delete reaction ${it.message}"
                    )
                }
            )
            .addTo(compositeDisposable)
    }

    private fun editMessage(messageId: Int, content: String) {
        messageRepository.editMessage(messageId, content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    Log.d(
                        javaClass.simpleName,
                        "error delete reaction ${it.message}"
                    )
                }
            )
            .addTo(compositeDisposable)
    }

    private fun editTopic(messageId: Int, newTopic: String) {
        messageRepository.editTopic(messageId, newTopic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    Log.d(
                        javaClass.simpleName,
                        "error delete reaction ${it.message}"
                    )
                }
            )
            .addTo(compositeDisposable)
    }

    private fun updateMessage(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long = DEFAULT_ANCHOR
    ) {
        messageRepository.updateMessage(
            streamTitle,
            topicTitle,
            anchor,
            NUM_BEFORE_UPDATE,
            NUM_AFTER
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                },
                onError = { exception ->
                    viewState.render(State.Error(exception.message))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun checkContent(_topicTitle: String): String {
        var topic = EMPTY_STRING
        if (topicTitle != null) {
            topic = topicTitle!!
        } else {
            if (_topicTitle.isEmpty()) {
                viewState.render(State.Error("Enter topic"))
            } else {
                topic = _topicTitle
            }
        }
        return topic
    }

    //if open stream then show date else show date and topicName
    private fun getMessagesByDivider(messages: List<MessageEntity>): List<ItemFingerPrint> {
        return topicTitle?.let {
            messages.groupBy { formatDateByDay(it.timestamp) }
                .flatMap { (date: String, messagesByDate: List<MessageEntity>) ->
                    listOf(DateDividerFingerPrint(date)) +
                            messagesByDate.map { message -> MessageFingerPrint(message) }
                }
        } ?: messages.groupBy { formatDateByDay(it.timestamp) }
            .flatMap { (date: String, messagesByDate: List<MessageEntity>) ->
                listOf(DateDividerFingerPrint(date)) +
                        getMessagesByDateAndTopicName(messagesByDate)
            }
    }

    private fun getMessagesByDateAndTopicName(messagesByDate: List<MessageEntity>): List<ItemFingerPrint> {
        return messagesByDate.flatMapIndexed { i: Int, message: MessageEntity ->
            if (i == 0 || messagesByDate[i - 1].topicName != message.topicName) {
                listOf(
                    DateDividerFingerPrint(message.topicName),
                    MessageFingerPrint(message)
                )
            } else {
                listOf(MessageFingerPrint(message))
            }
        }
    }

    companion object {

        private const val INITIAL_PAGE_SIZE = 20
        private const val NUM_BEFORE_UPDATE = 1
        private const val NUM_AFTER = 0
        private const val NUM_BEFORE_DELETE = 100
        private const val DEFAULT_ANCHOR = 10000000000000000
        private const val EMPTY_STRING = ""
    }
}