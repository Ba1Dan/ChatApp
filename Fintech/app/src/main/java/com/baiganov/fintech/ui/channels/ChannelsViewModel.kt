package com.baiganov.fintech.ui.channels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.baiganov.fintech.data.StreamRepository
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.model.Stream
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.util.State
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ChannelsViewModel(application: Application) : AndroidViewModel(application) {

    private var itemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()
    private var subscribedItemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()

    private val streamRepository: StreamRepository =
        StreamRepository(getApplication<Application>().applicationContext)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private val searchSubjectAll: PublishSubject<String> = PublishSubject.create()
    private var tabPosition = 0

    private var _allStreams: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()
    val mainScreenState: LiveData<State<List<ItemFingerPrint>>>
        get() = _allStreams

    private var _subscribeStreams: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()
    val subscribeStreams: LiveData<State<List<ItemFingerPrint>>>
        get() = _subscribeStreams

    init {
        getStreamsFromDb()
        getSubscribeStreamsFromDb()

        getAllStreams()

        searchSubscribeStreams()
        searchAllStreams()
    }

    private fun getSubscribeStreamsFromDb() {
        streamRepository.getSubscribedStreamsFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { streamEntities ->
                    val streams = streamEntities.map { stream ->
                        StreamFingerPrint(stream)
                    }
                    subscribedItemsOfRecycler.clear()
                    subscribedItemsOfRecycler.addAll(streams)

                    _subscribeStreams.value = State.Result(subscribedItemsOfRecycler)
                },
                onError = {
                    Log.d("xxx", "error from db")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun getStreamsFromDb() {
        streamRepository.getStreamsFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { streamEntities ->
                    val streams = streamEntities.map { stream ->
                        StreamFingerPrint(stream)
                    }

                    itemsOfRecycler.clear()
                    itemsOfRecycler.addAll(streams)
                    _allStreams.value = State.Result(itemsOfRecycler)
                },
                onError = {
                    Log.d("xxx", "error from db")
                }
            )
            .addTo(compositeDisposable)
    }

    fun searchTopics(searchQuery: String, position: Int) {
        tabPosition = position

        if (tabPosition == 0) {
            searchSubject.onNext(searchQuery)
        } else {
            searchSubjectAll.onNext(searchQuery)
        }
    }

    fun openStream(
        type: Int,
        position: Int,
        topics: List<TopicFingerPrint>,
    ) {
        if (type == 0) {
            //Subscibed
            subscribedItemsOfRecycler.addAll(position + 1, topics)
            _subscribeStreams.value = State.Result(subscribedItemsOfRecycler)
        } else {
            itemsOfRecycler.addAll(position + 1, topics)
            _allStreams.value = State.Result(itemsOfRecycler)
        }
    }

    fun closeStream(type: Int, topics: List<TopicFingerPrint>) {
        if (type == 0) {
            subscribedItemsOfRecycler.removeAll(topics)
            _subscribeStreams.value = State.Result(subscribedItemsOfRecycler)
        } else {
            itemsOfRecycler.removeAll(topics)
            _allStreams.value = State.Result(itemsOfRecycler)
        }
    }

    private fun getAllStreams() {
        streamRepository.getStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { getSubscribeStreams() },
                onError = { exception -> _allStreams.value = State.Error(exception.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun getSubscribeStreams() {
        _subscribeStreams.postValue(State.Loading())
        streamRepository.getSubscribedStreams()
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
            .doOnNext { _subscribeStreams.postValue(State.Loading()) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                streamRepository.searchSubscribedStreams()
                    .subscribeOn(Schedulers.io())
                    .flattenAsObservable { streamResponse ->
                        streamResponse.streams.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    .flatMapSingle { stream ->
                        streamRepository.getTopics(stream.id)
                            .zipWith(Single.just(stream)) { topicsResponse, _ ->
                                stream.apply {
                                    topics = topicsResponse.topics
                                }
                            }
                    }
                    .toList()
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
            .doOnNext { _allStreams.postValue(State.Loading()) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                streamRepository.searchStreams()
                    .subscribeOn(Schedulers.io())
                    .flattenAsObservable { streamResponse ->
                        streamResponse.streams.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    .flatMapSingle { stream ->
                        streamRepository.getTopics(stream.id)
                            .zipWith(Single.just(stream)) { topicsResponse, _ ->
                                stream.apply {
                                    topics = topicsResponse.topics
                                }
                            }
                    }
                    .toList()
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

    private fun mapToFingerPrint(list: List<Stream>): List<StreamFingerPrint> {
        return list.map { stream ->
            StreamFingerPrint(
                StreamEntity(
                    streamId = stream.id,
                    name = stream.name,
                    topics = stream.topics,
                    isSubscribed = true
                )
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