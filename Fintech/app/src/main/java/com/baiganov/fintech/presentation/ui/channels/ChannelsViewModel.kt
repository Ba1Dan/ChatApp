package com.baiganov.fintech.presentation.ui.channels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.domain.repositories.ChannelsRepository
import com.baiganov.fintech.presentation.util.Event.EventChannels
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.presentation.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChannelsViewModel @Inject constructor(
    private val channelsRepository: ChannelsRepository
) : ViewModel() {

    private var itemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()
    private var subscribedItemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private val searchSubjectAll: PublishSubject<String> = PublishSubject.create()

    private var _allStreams: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()
    val mainScreenState: LiveData<State<List<ItemFingerPrint>>>
        get() = _allStreams

    private var _subscribeStreams: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()
    val subscribeStreams: LiveData<State<List<ItemFingerPrint>>>
        get() = _subscribeStreams

    init {
        searchSubscribeStreams()
        searchAllStreams()

        getSubscribeStreams()
    }

    fun obtainEvent(event: EventChannels) {
        when (event) {
            is EventChannels.OpenStream -> {
                openStream(event.type, event.position, event.topics)
            }
            is EventChannels.CloseStream -> {
                closeStream(event.type, event.topics)
            }
            is EventChannels.SearchStreams -> {
                searchTopics(event.searchQuery, event.type)
            }
            else -> {
                Log.d(javaClass.simpleName, "Unknown event")
            }
        }
    }

    private fun searchTopics(searchQuery: String, type: Int) {
        if (type == 0) {
            searchSubject.onNext(searchQuery)
        } else {
            searchSubjectAll.onNext(searchQuery)
        }
    }

    private fun openStream(
        type: Int,
        position: Int,
        topics: List<TopicFingerPrint>,
    ) {
        if (type == 0) {
            subscribedItemsOfRecycler.addAll(position + 1, topics)
            _subscribeStreams.value = State.Result(subscribedItemsOfRecycler)
        } else {
            itemsOfRecycler.addAll(position + 1, topics)
            _allStreams.value = State.Result(itemsOfRecycler)
        }
    }

    private fun closeStream(type: Int, topics: List<TopicFingerPrint>) {
        if (type == 0) {
            subscribedItemsOfRecycler.removeAll(topics)
            _subscribeStreams.value = State.Result(subscribedItemsOfRecycler)
        } else {
            itemsOfRecycler.removeAll(topics)
            _allStreams.value = State.Result(itemsOfRecycler)
        }
    }

    private fun getAllStreams() {
        channelsRepository.getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { Functions.EMPTY_ACTION },
                onError = { exception -> _allStreams.value = State.Error(exception.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun getSubscribeStreams() {
        channelsRepository.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .doOnComplete { _subscribeStreams.postValue(State.Loading()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { getAllStreams() },
                onError = { exception -> _subscribeStreams.value = State.Error(exception.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun searchSubscribeStreams() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                channelsRepository.searchSubscribedStreams(searchQuery)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val list = mapToFingerPrint(it)

                    subscribedItemsOfRecycler.clear()
                    subscribedItemsOfRecycler.addAll(list)
                    _subscribeStreams.value = State.Result(subscribedItemsOfRecycler)

                },
                onError = { _subscribeStreams.value = State.Error(it.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun searchAllStreams() {
        searchSubjectAll
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                channelsRepository.searchStreams(searchQuery)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val list = mapToFingerPrint(it)

                    itemsOfRecycler.clear()
                    itemsOfRecycler.addAll(list)

                    _allStreams.value = State.Result(itemsOfRecycler)
                },
                onError = { _allStreams.value = State.Error(it.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun mapToFingerPrint(list: List<StreamEntity>): List<StreamFingerPrint> {
        return list.map { stream ->
            StreamFingerPrint(
                stream
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        const val INITIAL_QUERY: String = ""
    }
}