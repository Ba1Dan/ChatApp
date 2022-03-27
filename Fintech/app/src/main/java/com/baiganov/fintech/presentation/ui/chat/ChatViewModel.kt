package com.baiganov.fintech.presentation.ui.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.domain.usecase.chat.*
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.util.State
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import kotlin.properties.Delegates

class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessageUseCase: GetMessageUseCase,
    private val loadMessageUseCase: LoadMessageUseCase,
    private val loadNextMessagesUseCase: LoadNextMessagesUseCase,
    private val addReactionUseCase: AddReactionUseCase,
    private val deleteReactionUseCase: DeleteReactionUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val editTopicUseCase: EditTopicUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val updateMessageUseCase: UpdateMessageUseCase
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
        uploadFileUseCase.execute(uri, type, name)
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
        sendMessageUseCase.execute(streamId, message, topic)
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
        getMessages(topicTitle, streamId)

        loadMessageUseCase.execute(
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
        loadNextMessagesUseCase.execute(
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
        addReactionUseCase.execute(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    _state.value = (State.Error("error add reaction"))
                }
            )
            .addTo(compositeDisposable)
    }

    fun deleteReaction(
        messageId: Int,
        emojiName: String
    ) {
        deleteReactionUseCase.execute(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    _state.value = (State.Error("error delete reaction"))
                }
            )
            .addTo(compositeDisposable)
    }

    fun deleteMessage(
        streamId: Int,
        messageId: Int,
    ) {
        deleteMessageUseCase.execute(messageId)
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
                    _state.value = (State.Error("error delete message"))
                }
            )
            .addTo(compositeDisposable)
    }

    fun editMessage(messageId: Int, content: String) {
        editMessageUseCase.execute(messageId, content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    _state.value = (State.Error("error edit message"))
                }
            )
            .addTo(compositeDisposable)
    }

    fun editTopic(messageId: Int, newTopic: String) {
        editTopicUseCase.execute(messageId, newTopic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = {
                    _state.value = (State.Error("error edit topic"))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun getMessages(topicTitle: String?, streamId: Int) {
        getMessageUseCase.execute(topicTitle, streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { messages ->
                    if (messages.isEmpty() && isLoading) {
                        _state.value = State.Loading
                    } else {
                        _state.value = State.Result(messages)
                    }
                },
                onError = {
                    _state.value = State.Error(it.message)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun updateMessage(
        streamTitle: String,
        topicTitle: String?,
        anchor: Long = DEFAULT_ANCHOR
    ) {
        updateMessageUseCase.execute(
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

    companion object {
        private const val INITIAL_PAGE_SIZE = 20
        private const val NUM_BEFORE_UPDATE = 1
        private const val NUM_AFTER = 0
        private const val NUM_BEFORE_DELETE = 100
        private const val DEFAULT_ANCHOR = 10000000000000000
        private const val EMPTY_STRING = ""
    }
}