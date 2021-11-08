package com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints

import com.baiganov.fintech.R
import com.baiganov.fintech.model.Stream

class StreamFingerPrint(
    val stream: Stream
) : ItemFingerPrint {
    override val viewType: Int = R.layout.item_stream
    override val id: String = stream.name

    val childTopics: List<TopicFingerPrint> = stream.topics.map { topic -> TopicFingerPrint(topic, stream.id) }

    var isExpanded = false
}