package com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints

import com.baiganov.fintech.R
import com.baiganov.fintech.data.db.entity.StreamEntity

class StreamFingerPrint(
    val stream: StreamEntity
) : ItemFingerPrint {
    override val viewType: Int = R.layout.item_stream
    override val id: Int = stream.streamId

    val childTopics: List<TopicFingerPrint> = stream.topics.map { topic -> TopicFingerPrint(topic, stream.name, stream.streamId) }

    var isExpanded = false
}