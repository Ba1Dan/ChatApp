package com.baiganov.fintech.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.MessageRepository
import com.baiganov.fintech.ui.MainScreenState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ChatViewModel : ViewModel() {

    private val messageRepository = MessageRepository()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var _messages: MutableLiveData<MainScreenState> = MutableLiveData()
    val messages: LiveData<MainScreenState>
        get() = _messages

    fun loadMessage() {
        messageRepository.loadMessages()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _messages.postValue(MainScreenState.Loading) }
            .subscribeBy(
                onNext = { _messages.value = MainScreenState.Result(it) },
                onError = { _messages.value = MainScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    fun addMessage(message: String) {
        messageRepository.addMessage(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _messages.value = MainScreenState.Result(it) },
                onError = { _messages.value = MainScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    fun addEmoji(position: Int, emoji: String) {
        messageRepository.addEmoji(position, emoji)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _messages.value = MainScreenState.Result(it) },
                onError = { _messages.value = MainScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}