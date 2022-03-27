package com.baiganov.fintech.presentation.ui.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.presentation.util.State
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class ChannelsViewModel @Inject constructor(
    private val repository: ChannelsRepository,
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _state: MutableLiveData<State<String>> = MutableLiveData()

    val state: LiveData<State<String>>
        get() = _state

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun searchTopics(searchQuery: String) {
        _state.value = State.Result(searchQuery)
    }
}