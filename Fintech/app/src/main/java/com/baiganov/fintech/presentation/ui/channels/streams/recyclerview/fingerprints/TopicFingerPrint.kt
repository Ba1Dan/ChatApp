package com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints

import com.baiganov.fintech.R
import com.baiganov.fintech.model.Topic

class TopicFingerPrint(
    val topic: Topic,
    val streamTitle: String,
    val streamId: Int
) : ItemFingerPrint {

    override val viewType: Int = R.layout.item_topic
    override val id: Int = streamId
}