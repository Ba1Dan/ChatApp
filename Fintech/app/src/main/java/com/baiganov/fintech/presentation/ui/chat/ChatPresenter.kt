package com.baiganov.fintech.presentation.ui.chat

import android.net.Uri
import android.util.Log
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.domain.repository.MessageRepository
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
class ChatPresenter @Inject constructor(private val messageRepository: MessageRepository) :
    MvpPresenter<ChatView>() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()
    private var topicTitle: String? = null


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
                loadNextMessages(
                    streamTitle,
                    topicTitle,
                    event.anchor
                )
            }

            is Event.EventChat.AddReaction -> {
                addReaction(
                    event.messageId,
                    event.emojiName
                )
            }

            is Event.EventChat.DeleteReaction -> {
                deleteReaction(
                    event.messageId,
                    event.emojiName
                )
            }

            is Event.EventChat.SendMessage -> {
                sendMessage(
                    event.streamId,
                    event.message,
                    event.topicTitle,
                )
            }

            is Event.EventChat.DeleteMessage -> {
                deleteMessage(
                    event.streamId,
                    event.messageId
                )
            }

            is Event.EventChat.UploadFile -> {
                uploadFile(event.uri, event.type, event.name)
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
            .subscribe(
                { fileResponse ->
                    viewState.render(
                        State.AddFile("[${fileResponse.uri.split("/").last()}](${fileResponse.uri})"
                        )
                    )
                },
                { Log.d("upload_message", "error get messages from db  ${it.message}") }
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

                    if (mess.isEmpty()) {
                        viewState.render(State.Loading())
                    } else {
                        viewState.render(State.Result(mess))
                    }
                },
                onError = {
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
                onNext = { streamEntities ->

                    val mess = streamEntities.groupBy { formatDateByDay(it.timestamp) }
                        .flatMap { (date: String, messagesByDate: List<MessageEntity>) ->
                            listOf(DateDividerFingerPrint(date)) +
                                    messagesByDate.map { message -> MessageFingerPrint(message) }
                        }

                    if (mess.isEmpty()) {
                        viewState.render(State.Loading())
                    } else {
                        viewState.render(State.Result(mess))
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
        var topic = ""
        if (topicTitle != null) {
            topic = topicTitle!!
        } else {
            if (_topicTitle.isEmpty()) {
                viewState.render(State.Error("No input topic"))
            } else {
                topic = _topicTitle
            }
        }

        messageRepository.sendMessage(streamId, message, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topic)
                },
                onError = { Log.d(javaClass.simpleName, "error send message ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    private fun loadMessage(
        streamTitle: String,
        topicTitle: String?,
        streamId: Int,
        anchor: Long = DEFAULT_ANCHOR
    ) {
        messageRepository.loadMessages(
            streamTitle,
            topicTitle,
            anchor,
            streamId,
            INITIAL_PAGE_SIZE
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

    private fun loadNextMessages(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long
    ) {
        messageRepository.loadNextMessages(
            streamTitle,
            topicTitle,
            anchor,
            INITIAL_PAGE_SIZE
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
                        messageId.toLong()
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

    fun editMessage(messageId: Int, content: String) {
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

    private fun updateMessage(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long = DEFAULT_ANCHOR
    ) {
        messageRepository.updateMessage(
            streamTitle,
            topicTitle,
            anchor,
            NUM_BEFORE_UPDATE
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

    companion object {

        private const val INITIAL_PAGE_SIZE = 20
        private const val NUM_BEFORE_UPDATE = 1
        private const val DEFAULT_ANCHOR = 10000000000000000
    }
}