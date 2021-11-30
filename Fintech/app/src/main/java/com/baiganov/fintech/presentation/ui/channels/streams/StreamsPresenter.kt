package com.baiganov.fintech.presentation.ui.channels.streams

import android.util.Log
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.presentation.ui.channels.ChannelsPages
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import io.reactivex.Completable
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

    private var itemsOfRecycler: MutableList<ItemFingerPrint> = mutableListOf()
    private val compositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    var tabPosition: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getStreams()
    }

    init {
        searchSubscribeStreams()
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
            else -> {
                Log.d(javaClass.simpleName, "Unknown event")
            }
        }
    }

    private fun searchTopics(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    private fun getStreams() {
        getStreamsByTabPosition()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe {
//                viewState.render(ChatState.Loading())
//            }
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                },
                onError = { exception ->
                    viewState.render(State.Error(exception.message))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun openStream(
        position: Int,
        topics: List<TopicFingerPrint>,
    ) {
        itemsOfRecycler.addAll(position + 1, topics)
        viewState.render(State.Result(itemsOfRecycler))
    }

    private fun closeStream(topics: List<TopicFingerPrint>) {
        itemsOfRecycler.removeAll(topics)
        viewState.render(State.Result(itemsOfRecycler))
    }

    private fun getStreamsByTabPosition(): Completable = when (tabPosition) {
        ChannelsPages.SUBSCRIBED.ordinal -> repository.getSubscribedStreams()
        ChannelsPages.ALL_STREAMS.ordinal -> repository.getAllStreams()
        else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $tabPosition")
    }

    private fun searchSubscribeStreams() {
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
                    val list = mapToFingerPrint(it)

                    itemsOfRecycler.clear()
                    itemsOfRecycler.addAll(list)
                    viewState.render(State.Result(itemsOfRecycler))

                },
                onError = {
                    viewState.render(State.Error(it.message)) }
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