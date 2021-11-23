package com.baiganov.fintech.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.MessageRepository
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ChatViewModel(private val messageRepository: MessageRepository) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var _messages: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()

    val messages: LiveData<State<List<ItemFingerPrint>>>
        get() = _messages

    fun getMessagesFromDb(topicName: String, streamId: Int) {
        messageRepository.getMessagesFromDb(topicName, streamId)
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

    fun sendMessage(stream: String, streamId: Int, topic: String, message: String) {
        messageRepository.sendMessage(streamId, message, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    updateMessage(stream, topic)
                },
                onError = { Log.d(javaClass.simpleName, "error send message ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    fun loadMessage(stream: String, topic: String, streamId: Int, anchor: Long = DEFAULT_ANCHOR) {
        messageRepository.loadMessages(stream, topic, anchor, streamId)
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

    fun loadNextMessages(stream: String, topic: String, streamId: Int, anchor: Long) {
        messageRepository.loadNextMessages(stream, topic, anchor)
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

    fun addReaction(messageId: Int, emojiName: String, streamTitle: String, topicTitle: String) {
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

    fun deleteReaction(messageId: Int, emojiName: String, streamTitle: String, topicTitle: String) {
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

    private fun updateMessage(stream: String, topic: String, anchor: Long = DEFAULT_ANCHOR) {
        messageRepository.updateMessage(stream, topic, anchor)
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

        private const val DEFAULT_ANCHOR = 10000000000000000
    }
}