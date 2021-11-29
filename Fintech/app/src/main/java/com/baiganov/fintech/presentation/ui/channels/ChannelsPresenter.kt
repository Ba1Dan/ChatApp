package com.baiganov.fintech.presentation.ui.channels

import android.util.Log
import com.baiganov.fintech.domain.repositories.ChannelsRepository
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import io.reactivex.disposables.CompositeDisposable
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class ChannelsPresenter @Inject constructor(private val repository: ChannelsRepository) :
    MvpPresenter<ChannelsView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun obtainEvent(event: Event.EventChannels) {
        when (event) {
            is Event.EventChannels.SearchStreams -> {
                searchTopics(event.searchQuery)
            }
            else -> {
                Log.d(javaClass.simpleName, "Unknown event")
            }
        }
    }

    private fun searchTopics(searchQuery: String) {
        viewState.render(State.Result(searchQuery))
    }
}