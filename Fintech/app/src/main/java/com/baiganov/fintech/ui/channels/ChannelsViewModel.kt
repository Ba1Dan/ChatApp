package com.baiganov.fintech.ui.channels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.StreamRepository
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.model.Stream
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.util.State
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ChannelsViewModel(application: Application) : AndroidViewModel(application) {

    private var itemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()
    private var subscribedItemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()

    private var isEmptyDb = false

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
        subscribeToSearchSubscribeStreams()
        subscribeToSearchAllStreams()
    }

    private fun getSubscribeStreamsFromDb() {
        streamRepository.getSubscribedStreamsFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { streamEntities ->
                    Log.d("xxx", "sub from db")
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
                    Log.d("xxx", "all from db")
                    val streams = streamEntities.map { stream ->
                        StreamFingerPrint(stream)
                    }

                    isEmptyDb = streams.isEmpty()
                    Log.d("xxx", "isEmpty db ${isEmptyDb}")
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
            Log.d("xxx", subscribedItemsOfRecycler.size.toString())
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

    private fun subscribeToSearchAllStreams() {
        streamRepository.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { Functions.EMPTY_ACTION },
                onError = { exception -> _allStreams.value = State.Error(exception.message) }
            )
            .addTo(compositeDisposable)
    }

    private fun subscribeToSearchSubscribeStreams() {
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