package com.baiganov.fintech.data

import android.util.Log
import com.baiganov.fintech.model.Stream
import com.baiganov.fintech.model.Topic
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class Repository {

    private var streams = mutableListOf<ItemFingerPrint>(
        StreamFingerPrint(
            Stream(
                0,
                "#general",
                listOf(Topic(0, "Testing"), Topic(1, "Bruh")),
                true
            )
        ),
        StreamFingerPrint(
            Stream(
                0,
                "#Development",
                listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")),
                true
            )
        ),
        StreamFingerPrint(Stream(0, "#Design", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
        StreamFingerPrint(Stream(0, "#PR", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
        StreamFingerPrint(Stream(0, "test3", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
    )

    var subscribedStreams: MutableList<ItemFingerPrint> = streams.filter { (it as StreamFingerPrint).stream.isSubscribed} as MutableList<ItemFingerPrint>

    fun loadAllStreams(searchQuery: String): Observable<List<ItemFingerPrint>> {
        Log.d("xxx", "load all")
        return Observable.fromCallable { generateStreams() }
            .delay(1000L, TimeUnit.MILLISECONDS)
            .map { streams ->
                streams.filter {
                    (it as StreamFingerPrint).stream.name.contains(searchQuery, ignoreCase = true)
                }
            }
    }

    fun loadSubscribedStreams(searchQuery: String): Observable<List<ItemFingerPrint>> {
        Log.d("xxx", "load subscribe")
        return Observable.fromCallable { subscribedStreams as List<ItemFingerPrint>}
            .delay(1000L, TimeUnit.MILLISECONDS)
    }

    fun add(type: Int, position: Int, topics: List<TopicFingerPrint>): Observable<List<ItemFingerPrint>> {
        return if (type == 1) {
            streams = ArrayList(streams)
            streams.addAll(position + 1, topics)
            Observable.fromCallable { streams }
        } else {
            subscribedStreams = ArrayList(subscribedStreams)
            subscribedStreams.addAll(position + 1, topics)
            Observable.fromCallable { subscribedStreams }
        }
    }

    fun remove(type: Int, topics: List<TopicFingerPrint>): Observable<List<ItemFingerPrint>> {
        return if (type == 1) {
            streams = ArrayList(streams)
            streams.removeAll(topics)
            Observable.fromCallable { streams }
        } else {
            subscribedStreams = ArrayList(subscribedStreams)
            subscribedStreams.removeAll(topics)
            Observable.fromCallable { subscribedStreams }
        }

    }

    private fun generateStreams(): List<ItemFingerPrint> {
        return streams
    }
}