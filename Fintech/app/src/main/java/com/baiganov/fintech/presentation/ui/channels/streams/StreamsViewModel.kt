package com.baiganov.fintech.presentation.ui.channels.streams

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.domain.usecase.channels.CreateStreamUseCase
import com.baiganov.fintech.domain.usecase.channels.GetStreamsUseCase
import com.baiganov.fintech.domain.usecase.channels.SearchStreamsUseCase
import com.baiganov.fintech.presentation.NetworkManager
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.StreamFingerPrint
import com.baiganov.fintech.presentation.model.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.channels.ChannelsPages
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
import kotlin.properties.Delegates

class StreamsViewModel @Inject constructor(
    private val networkManager: NetworkManager,
    private val searchStreamsUseCase: SearchStreamsUseCase,
    private val getStreamsUseCase: GetStreamsUseCase,
    private val createStreamUseCase: CreateStreamUseCase
) : ViewModel() {

    private var itemsOfRecycler: List<ItemFingerPrint> = mutableListOf()
    private val compositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    private var tabPosition by Delegates.notNull<Int>()
    private var isLoading = true

    private val _state: MutableLiveData<State<List<ItemFingerPrint>>> = MutableLiveData()

    val state: LiveData<State<List<ItemFingerPrint>>>
        get() = _state

    fun init(tabPosition: Int) {
        this.tabPosition = tabPosition
        getStreams(tabPosition)
    }

    init {
        searchStreams()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun searchTopics(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    fun openStream(
        position: Int,
        topics: List<TopicFingerPrint>,
    ) {
        itemsOfRecycler = itemsOfRecycler.toMutableList().apply {
            addAll(position + 1, topics)
        }
        (itemsOfRecycler[position] as StreamFingerPrint).isExpanded = true

        _state.value = State.Result(itemsOfRecycler)
    }

    fun closeStream(
        topicUI: List<TopicFingerPrint>
    ) {
        val topics = topicUI.map { it.topic }

        itemsOfRecycler = itemsOfRecycler.toMutableList().apply {
            removeAll { itemUi -> itemUi is TopicFingerPrint && itemUi.topic in topics }
        }

        _state.value = State.Result(itemsOfRecycler)
    }

    fun getStreams(type: Int) {
        if (networkManager.isConnected().value) {
            getStreamsUseCase.execute(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    _state.value = State.Loading
                }
                .subscribeBy(
                    onComplete = {
                        Functions.EMPTY_ACTION
                        isLoading = false
                    },
                    onError = { exception ->
                        _state.value = State.Error(exception.message)
                    }
                )
                .addTo(compositeDisposable)
        } else {
            _state.value = State.Error("No connection")
        }
    }

    fun createStream(name: String, description: String) {
        createStreamUseCase.execute(name, description)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    getStreams(ChannelsPages.SUBSCRIBED.ordinal)
                },
                onError = { exception ->
                    _state.value = State.Error(exception.message)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun searchStreams() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                searchStreamsUseCase.execute(searchQuery, tabPosition)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {streams ->
                    if (streams.isEmpty() && isLoading) {
                        _state.value = State.Loading
                    } else {
                        itemsOfRecycler = streams
                        _state.value = State.Result(itemsOfRecycler)
                    }
                },
                onError = {
                    _state.value = State.Error(it.message)
                }
            )
            .addTo(compositeDisposable)
    }

    companion object {
        const val INITIAL_QUERY = ""
    }
}