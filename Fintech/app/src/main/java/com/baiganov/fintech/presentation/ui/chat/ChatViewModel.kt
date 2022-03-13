package com.baiganov.fintech.presentation.ui.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.domain.repository.MessageRepository
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.presentation.ui.chat.recyclerview.DateDividerFingerPrint
import com.baiganov.fintech.util.State
import com.baiganov.fintech.util.formatDateByDay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.properties.Delegates

class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val _state: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()

    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()
    private var topicTitle: String? = null
    private var isLoading = true

    val state: LiveData<State<List<ItemFingerPrint>>>
        get() = _state

    var isConnected: Boolean = false

    fun init(streamTitle: String, streamId: Int, topicTitle: String?) {
        this.streamTitle = streamTitle
        this.topicTitle = topicTitle
        this.streamId = streamId
    }

    fun uploadFile(uri: Uri, type: String, name: String) {
        messageRepository.uploadFile(uri, type, name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _state.value = State.LoadingFile
            }
            .subscribe(
                { fileResponse ->
                    _state.value =
                        State.AddFile(
                            "[${fileResponse.uri.split("/").last()}](${fileResponse.uri})"
                        )
                },
                {
                    _state.value = State.Error(it.message)
                }
            )
            .addTo(compositeDisposable)
    }

    fun sendMessage(
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
                    _state.value = (State.Error("error send message"))
                    Log.d(javaClass.simpleName, "${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    fun loadMessage(
        streamId: Int,
        anchor: Long = DEFAULT_ANCHOR,
        numAfter: Int = NUM_AFTER
    ) {
        topicTitle?.let { getMessagesFromDb(it, streamId) } ?: getStreamMessages(streamId)
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
                    _state.value = (State.Error(exception.message))
                }
            )
            .addTo(compositeDisposable)
    }

    fun loadNextMessages(
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
                    _state.value = (State.Error(exception.message))
                }
            )
            .addTo(compositeDisposable)
    }

    fun addReaction(
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

    fun deleteReaction(
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

    fun deleteMessage(
        streamId: Int,
        messageId: Int,
    ) {
        messageRepository.deleteMessage(messageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    loadMessage(
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

    fun editTopic(messageId: Int, newTopic: String) {
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
                        _state.value = State.Loading
                    } else {
                        _state.value = State.Result(mess)
                    }
                },
                onError = {
                    _state.value = State.Error(it.message)
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
                        _state.value = (State.Loading)
                    } else {
                        val messagesFingerPrint = getMessagesByDivider(messages)
                        _state.value = (State.Result(messagesFingerPrint))
                    }
                },
                onError = {
                    Log.d(javaClass.simpleName, "error get messages from db  ${it.message}")
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
                    _state.value = (State.Error(exception.message))
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
                _state.value = (State.Error("Enter topic"))
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