package com.baiganov.fintech.data

import android.util.Log
import com.baiganov.fintech.model.Stream
import com.baiganov.fintech.model.Topic
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class StreamRepository {

    private val dataManager = DataManager()

    fun loadAllStreams(searchQuery: String): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.streams }
            .delay(1000L, TimeUnit.MILLISECONDS)
            .map { streams ->
                streams.filter {
                    it is StreamFingerPrint &&
                    it.stream.name.contains(searchQuery, ignoreCase = true)
                }
            }
    }

    fun loadSubscribedStreams(searchQuery: String): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.subscribedStreams as List<ItemFingerPrint> }
            .delay(1000L, TimeUnit.MILLISECONDS)
            .map { streams ->
            streams.filter {
                it is StreamFingerPrint &&
                it.stream.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    fun openStream(type: Int, position: Int, topics: List<TopicFingerPrint>): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.add(type, position, topics) }
    }

    fun closeStream(type: Int, topics: List<TopicFingerPrint>): Observable<List<ItemFingerPrint>> {
        return Observable.fromCallable { dataManager.remove(type, topics) }
    }
}