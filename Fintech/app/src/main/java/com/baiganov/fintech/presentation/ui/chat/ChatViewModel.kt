package com.baiganov.fintech.presentation.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.domain.repositories.MessageRepository
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.presentation.util.Event.*
import com.baiganov.fintech.presentation.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.presentation.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var _messages: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()

    val messages: LiveData<State<List<ItemFingerPrint>>>
        get() = _messages

    fun obtainEvent(event: EventChat) {

        when (event) {
            is EventChat.LoadFirstMessages -> {
                getMessagesFromDb(
                    event.topicTitle,
                    event.streamId
                )
                loadMessage(
                    event.streamTitle,
                    event.topicTitle,
                    event.streamId
                )
            }

            is EventChat.LoadNextMessages -> {
                loadNextMessages(
                    event.streamTitle,
                    event.topicTitle,
                    event.anchor
                )
            }

            is EventChat.AddReaction -> {
                addReaction(
                    event.streamTitle,
                    event.topicTitle,
                    event.messageId,
                    event.emojiName
                )
            }

            is EventChat.DeleteReaction -> {
                deleteReaction(
                    event.streamTitle,
                    event.topicTitle,
                    event.messageId,
                    event.emojiName
                )
            }

            is EventChat.SendMessage -> {
                sendMessage(
                    event.streamTitle,
                    event.topicTitle,
                    event.streamId,
                    event.message
                )
            }

            is EventChat.DeleteMessage -> {
                deleteMessage(
                    event.streamTitle,
                    event.topicTitle,
                    event.messageId,
                )
            }

            is EventChat.UploadFile -> {

            }

            else -> {
                Log.d(javaClass.simpleName, "unknown type event")
            }
        }
    }

    private fun getMessagesFromDb(topicTitle: String, streamId: Int) {
        messageRepository.getMessagesFromDb(topicTitle, streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { streamEntities ->
                    val messages = streamEntities.map { message ->
                        MessageFingerPrint(message)
                    }
                    _messages.value = State.Result(messages)
                },
                onError = {
                    Log.d(javaClass.simpleName, "error get messages from db  ${it.message}")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun sendMessage(
        streamTitle: String,
        topicTitle: String,
        streamId: Int,
        message: String
    ) {
        messageRepository.sendMessage(streamId, message, topicTitle)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle)
                },
                onError = { Log.d(javaClass.simpleName, "error send message ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    private fun loadMessage(
        streamTitle: String,
        topicTitle: String,
        streamId: Int,
        anchor: Long = DEFAULT_ANCHOR
    ) {
        messageRepository.loadMessages(streamTitle, topicTitle, anchor, streamId, INITIAL_PAGE_SIZE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _messages.postValue(State.Loading()) }
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                },
                onError = { exception ->
                    _messages.value = State.Error(exception.message)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loadNextMessages(streamTitle: String, topicTitle: String, anchor: Long) {
        messageRepository.loadNextMessages(streamTitle, topicTitle, anchor, INITIAL_PAGE_SIZE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                },
                onError = { exception ->
                    _messages.value = State.Error(exception.message)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun addReaction(
        streamTitle: String,
        topicTitle: String,
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
                onError = { Log.d(javaClass.simpleName, "error add reaction ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    private fun deleteReaction(
        streamTitle: String,
        topicTitle: String,
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
                onError = { Log.d(javaClass.simpleName, "error delete reaction ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    //query does not have permission to delete the message
    private fun deleteMessage(
        streamTitle: String,
        topicTitle: String,
        messageId: Int,
    ) {
        messageRepository.deleteMessage(messageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(streamTitle, topicTitle, messageId.toLong())
                },
                onError = { Log.d(javaClass.simpleName, "error delete reaction ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    private fun updateMessage(
        streamTitle: String,
        topicTitle: String,
        anchor: Long = DEFAULT_ANCHOR
    ) {
        messageRepository.updateMessage(streamTitle, topicTitle, anchor, NUM_BEFORE_UPDATE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                },
                onError = {
                    _messages.value = State.Error(it.message)
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {

        private const val INITIAL_PAGE_SIZE = 20
        private const val NUM_BEFORE_UPDATE = 1
        private const val DEFAULT_ANCHOR = 10000000000000000
    }
}