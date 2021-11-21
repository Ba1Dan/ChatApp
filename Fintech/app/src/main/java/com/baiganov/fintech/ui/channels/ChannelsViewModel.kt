package com.baiganov.fintech.ui.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.StreamRepository
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.ui.Event.EventChannels
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ChannelsViewModel(private val streamRepository: StreamRepository) : ViewModel() {

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

        getAllStreams()
    }

    fun obtainEvent(event: EventChannels) {
        when (event) {

            is EventChannels.LoadStreams -> {

            }

            is EventChannels.OpenStream -> {
                openStream(event.type, event.position, event.topics)
            }

            is EventChannels.CloseStream -> {
                closeStream(event.type, event.topics)
            }

            is EventChannels.SearchStreams -> {
                searchTopics(event.searchQuery, event.type)
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
        streamRepository.getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { getSubscribeStreams() },
                onError = { exception -> _allStreams.value = State.Error(exception.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun getSubscribeStreams() {
        streamRepository.getSubscribedStreams()
            .doOnComplete { _subscribeStreams.postValue(State.Loading()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { Functions.EMPTY_ACTION },
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
                streamRepository.searchSubscribedStreams(searchQuery)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val list = mapToFingerPrint(it)

                    subscribedItemsOfRecycler.clear()
                    subscribedItemsOfRecycler.addAll(list)
                    _subscribeStreams.value = State.Result(list)

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
                streamRepository.searchStreams(searchQuery)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val list = mapToFingerPrint(it)

                    itemsOfRecycler.clear()
                    itemsOfRecycler.addAll(list)
                    _allStreams.value = State.Result(list)
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
        private const val INITIAL_PAGE_SIZE = 50
        private const val NEWEST_ANCHOR_MESSAGE = 10000000000000000
    }
}