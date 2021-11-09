package com.baiganov.fintech.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.MessageRepository
import com.baiganov.fintech.util.State
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ChatViewModel : ViewModel() {

    private val messageRepository = MessageRepository()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var _messages: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()
    val messages: LiveData<State<List<ItemFingerPrint>>>
        get() = _messages


    fun sendMessage(stream: String, streamId: Int, topic: String, message: String) {
        messageRepository.sendMessage(streamId, message, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.d("xxx", "отправлено")
                    updateMessages(stream, topic)
                },
                onError = { Log.d("xxx", "ошибка ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    private fun updateMessages(stream: String, topic: String) {
        messageRepository.loadMessages(stream, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    Log.d("xxx", "messages ${it.messages.size}")
                    val list = it.messages.map { message ->
                        MessageFingerPrint(message)
                    }
                    _messages.value = State.Result(list)
                },
                onError = {
                    Log.d("xxx", "error  ${it.message}")
                    _messages.value = State.Error(it.message)
                }
            )
            .addTo(compositeDisposable)
    }

    fun loadMessage(stream: String, topic: String) {
        messageRepository.loadMessages(stream, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _messages.postValue(State.Loading()) }
            .subscribeBy(
                onSuccess = {
                    Log.d("xxx", "messages ${it.messages.size}")
                    val list = it.messages.map { message ->
                        MessageFingerPrint(message)
                    }
                    _messages.value = State.Result(list)
                },
                onError = {
                    Log.d("xxx", "error  ${it.message}")
                    _messages.value = State.Error(it.message)
                }
            )
            .addTo(compositeDisposable)
    }

    fun addReaction(messageId: Int, emojiName: String, streamTitle: String, topicTitle: String) {
        Log.d("xxx", "emoji $emojiName")
        messageRepository.addReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.d("xxx", "добавлен эмодзи")
                    updateMessages(streamTitle, topicTitle)
                },
                onError = { Log.d("xxx", "ошибка ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    fun deleteReaction(messageId: Int, emojiName: String) {
        messageRepository.deleteReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    Log.d("xxx", "удален эмодзи")
                },
                onError = { Log.d("xxx", "ошибка ${it.message}") }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}