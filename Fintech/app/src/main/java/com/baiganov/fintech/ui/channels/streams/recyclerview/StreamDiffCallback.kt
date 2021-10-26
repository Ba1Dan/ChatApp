package com.baiganov.fintech.ui.channels.streams.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class StreamDiffCallback : DiffUtil.ItemCallback<ItemFingerPrint>() {

    override fun areItemsTheSame(oldItem: ItemFingerPrint, newItem: ItemFingerPrint): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ItemFingerPrint, newItem: ItemFingerPrint): Boolean {
        return when {
            oldItem is StreamFingerPrint && newItem is StreamFingerPrint -> oldItem.stream == newItem.stream && oldItem.isExpanded == newItem.isExpanded
            oldItem is TopicFingerPrint && newItem is TopicFingerPrint -> oldItem.topic == newItem.topic
            else -> true
        }
    }

}