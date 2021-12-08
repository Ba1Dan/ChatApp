package com.baiganov.fintech.presentation.ui.channels.streams

import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.StreamFingerPrint
import com.baiganov.fintech.presentation.model.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.channels.ChannelsPages
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class StreamsPresenter @Inject constructor(private val repository: ChannelsRepository) :
    MvpPresenter<StreamsView>() {

    private var itemsOfRecycler: List<ItemFingerPrint> = mutableListOf()
    private val compositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    private var isGettingAllStreams = true

    var tabPosition: Int? = null

    init {
        searchStreams()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun obtainEvent(event: Event.EventChannels) {
        when (event) {
            is Event.EventChannels.OpenStream -> {
                openStream(event.position, event.topics)
            }
            is Event.EventChannels.CloseStream -> {
                closeStream(event.topics)
            }
            is Event.EventChannels.SearchStreams -> {
                searchTopics(event.searchQuery)
            }
            is Event.EventChannels.CreateStream -> {
                createStream(event.streamName, event.streamDescription)
            }
        }
    }

    private fun searchTopics(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    private fun openStream(
        position: Int,
        topics: List<TopicFingerPrint>,
    ) {
        itemsOfRecycler = itemsOfRecycler.toMutableList().apply {
            addAll(position + 1, topics)
        }
        (itemsOfRecycler[position] as StreamFingerPrint).isExpanded = true

        viewState.render(State.Result(itemsOfRecycler))
    }

    private fun closeStream(topicUI: List<TopicFingerPrint>) {
        val topics = topicUI.map { it.topic }

        itemsOfRecycler = itemsOfRecycler.toMutableList().apply {
            removeAll { itemUi -> itemUi is TopicFingerPrint && itemUi.topic in topics }
        }

        viewState.render(State.Result(itemsOfRecycler))
    }

    private fun searchStreams() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                repository.searchStreams(searchQuery, tabPosition)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (isGettingAllStreams) {
                        getStreams(ChannelsPages.SUBSCRIBED.ordinal)
                    }

                    val list = mapToFingerPrint(it)
                    itemsOfRecycler = ArrayList(list)
                    viewState.render(State.Result(itemsOfRecycler))


                },
                onError = {
                    viewState.render(State.Error(it.message))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun getStreams(type: Int) {
        repository.getStreams(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                viewState.render(State.Loading())
            }
            .subscribeBy(
                onComplete = {
                    if (isGettingAllStreams) {
                        getStreams(ChannelsPages.ALL_STREAMS.ordinal)
                        isGettingAllStreams = false
                    } else {
                        Functions.EMPTY_ACTION
                    }
                },
                onError = { exception ->
                    viewState.render(State.Error(exception.message))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun createStream(name: String, description: String) {
        repository.createStream(name, description)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribeBy(
                onComplete = {
                    getStreams(ChannelsPages.SUBSCRIBED.ordinal)
                },
                onError = { exception ->
                    viewState.render(State.Error(exception.message))
                }
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

    companion object {
        const val INITIAL_QUERY = ""
    }
}