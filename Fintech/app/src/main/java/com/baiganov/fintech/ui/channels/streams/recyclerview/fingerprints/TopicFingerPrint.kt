package com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints

import com.baiganov.fintech.R
import com.baiganov.fintech.model.Topic
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class TopicFingerPrint(
    val topic: Topic,
//    val streamTitle: String,
    val streamId: Int
) : ItemFingerPrint {

    override val viewType: Int = R.layout.item_topic
    override val id: String = streamId.toString()
}