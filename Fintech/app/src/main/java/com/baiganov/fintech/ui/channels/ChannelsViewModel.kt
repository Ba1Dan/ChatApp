package com.baiganov.fintech.ui.channels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.StreamRepository
import com.baiganov.fintech.model.Stream
import com.baiganov.fintech.util.State
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

import java.util.concurrent.TimeUnit

class ChannelsViewModel : ViewModel() {


    private var itemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()

    private val streamRepository: StreamRepository = StreamRepository()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private val searchSubject1: PublishSubject<String> = PublishSubject.create()
    private var tabPosition = 0

    private var _allStreams: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()
    val mainScreenState: LiveData<State<List<ItemFingerPrint>>>
        get() = _allStreams

    private var _subscribeStreams: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()
    val subscribeStreams: LiveData<State<List<ItemFingerPrint>>>
        get() = _subscribeStreams

    init {
        subscribeToSearchSubscribeStreams()
        subscribeToSearchAllStreams()
    }

    fun searchTopics(searchQuery: String, position: Int) {
        tabPosition = position
        if (tabPosition == 0) {
            searchSubject.onNext(searchQuery)
        } else {
            searchSubject1.onNext(searchQuery)
        }
    }

    private fun subscribeToSearchAllStreams() {
        streamRepository.getStreams()
            .subscribeOn(Schedulers.io())
            .flattenAsObservable {  streamResponse ->
                streamResponse.streams }
            .flatMapSingle { stream ->
                streamRepository.getTopics(stream.id)
                    .zipWith(Single.just(stream)) { topicsResponse, _ ->
                        stream.apply {
                            topics = topicsResponse.topics
                        }
                    }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .toList()
            .subscribeBy(
                onSuccess = {
                    val list = it.map { stream ->
                        StreamFingerPrint(stream)
                    }
                    itemsOfRecycler.addAll(list)
                    _allStreams.value = State.Result(list)
                    Log.d("xxx", "size ${list[0].childTopics.size}")
                },
                onError = { Log.d("xxx", it.message.toString()) }
            )
            .addTo(compositeDisposable)
    }

    fun openStream(
        type: Int,
        position: Int,
        topics: List<TopicFingerPrint>,
        stream: StreamFingerPrint
    ) {
        if (type == 0) {
            //Subscibed
            streamRepository.getTopics(stream.stream.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        val list = it.topics.map { topic ->
                            TopicFingerPrint(topic, stream.stream.id)
                        }

                        _allStreams.value = State.Result(list)
                        Log.d("xxx", "size ${list.size}")
                    },
                    onError = { Log.d("xxx", it.message.toString()) }
                )
                .addTo(compositeDisposable)
        } else {
            itemsOfRecycler.addAll(position + 1, topics)
            _allStreams.value = State.Result(itemsOfRecycler)
        }
    }

    fun closeStream(type: Int, topics: List<TopicFingerPrint>) {
        if (type == 0) {
            streamRepository.closeStream(type, topics)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { _subscribeStreams.value = State.Result(it) },
                    onError = { _subscribeStreams.value = State.Error(it.message) }
                )
                .addTo(compositeDisposable)
        } else {
            itemsOfRecycler.removeAll(topics)
            _allStreams.value = State.Result(itemsOfRecycler)
        }
    }

    private fun subscribeToSearchSubscribeStreams() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _subscribeStreams.postValue(State.Loading()) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery -> streamRepository.loadSubscribedStreams(searchQuery) }
//            .map(topicToItemMapper)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _subscribeStreams.value = State.Result(it) },
                onError = { _subscribeStreams.value = State.Error(it.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun subscribeToSearchAllStreamsDeprecated() {
//        searchSubject1
//            .subscribeOn(Schedulers.io())
//            .distinctUntilChanged()
//            .doOnNext { _allStreams.postValue(State.Loading()) }
//            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
//            .switchMap { searchQuery -> streamRepository.loadAllStreams(searchQuery) }
////            .map(topicToItemMapper)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy(
//                onNext = { _allStreams.value = State.Result(it) },
//                onError = { _allStreams.value = State.Error(it.message) }
//            )
//            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        const val INITIAL_QUERY: String = ""
    }
}