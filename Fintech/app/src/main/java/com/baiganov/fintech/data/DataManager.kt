package com.baiganov.fintech.data


import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.model.Stream
import com.baiganov.fintech.model.Topic
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint

class DataManager {

    var data = mutableListOf<ItemFingerPrint>(
        StreamFingerPrint(Stream(0, "general", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
        StreamFingerPrint(Stream(0, "test", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
        StreamFingerPrint(Stream(0, "test1", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
        StreamFingerPrint(Stream(0, "test2", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
        StreamFingerPrint(Stream(0, "test3", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
    )

    fun add(position: Int, topics: List<TopicFingerPrint>): List<ItemFingerPrint> {
        data = ArrayList(data)
        data.addAll(position + 1, topics)
        return data
    }

    fun remove(position: Int, topics: List<TopicFingerPrint>): List<ItemFingerPrint> {
        data = ArrayList(data)
        data.removeAll(topics)
        return data
    }


}