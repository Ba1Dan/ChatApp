package com.baiganov.fintech.ui.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.StreamRepository
import com.baiganov.fintech.ui.MainScreenState
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ChannelsViewModel : ViewModel() {

    private val streamRepository: StreamRepository = StreamRepository()
    private var _allStreams: MutableLiveData<MainScreenState> = MutableLiveData()
    val mainScreenState: LiveData<MainScreenState>
        get() = _allStreams

    private var _subscribeStreams: MutableLiveData<MainScreenState> = MutableLiveData()
    val subscribeStreams: LiveData<MainScreenState>
        get() = _subscribeStreams

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private val searchSubject1: PublishSubject<String> = PublishSubject.create()
    private var tabPosition = 0


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

    fun openStream(type: Int, position: Int, topics: List<TopicFingerPrint>) {
        if (type == 0) {
            streamRepository.openStream(type, position, topics)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { _subscribeStreams.value = MainScreenState.Result(it) },
                    onError = { _subscribeStreams.value = MainScreenState.Error(it) }
                )
                .addTo(compositeDisposable)
        } else {
            streamRepository.openStream(type, position, topics)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { _allStreams.value = MainScreenState.Result(it) },
                    onError = { _allStreams.value = MainScreenState.Error(it) }
                )
                .addTo(compositeDisposable)
        }

    }

    fun closeStream(type: Int, topics: List<TopicFingerPrint>) {
        if (type == 0) {
            streamRepository.closeStream(type, topics)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { _subscribeStreams.value = MainScreenState.Result(it) },
                    onError = { _subscribeStreams.value = MainScreenState.Error(it) }
                )
                .addTo(compositeDisposable)
        } else {
            streamRepository.closeStream(type, topics)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { _allStreams.value = MainScreenState.Result(it) },
                    onError = { _allStreams.value = MainScreenState.Error(it) }
                )
                .addTo(compositeDisposable)
        }
    }

    private fun subscribeToSearchSubscribeStreams() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _subscribeStreams.postValue(MainScreenState.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery -> streamRepository.loadSubscribedStreams(searchQuery) }
//            .map(topicToItemMapper)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _subscribeStreams.value = MainScreenState.Result(it) },
                onError = { _subscribeStreams.value = MainScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    private fun subscribeToSearchAllStreams() {
        searchSubject1
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _allStreams.postValue(MainScreenState.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery -> streamRepository.loadAllStreams(searchQuery) }
//            .map(topicToItemMapper)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _allStreams.value = MainScreenState.Result(it) },
                onError = { _allStreams.value = MainScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        const val INITIAL_QUERY: String = ""
    }
}