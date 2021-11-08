package com.baiganov.fintech.data


import com.baiganov.fintech.model.*
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.DateDividerFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.ui.people.adapters.UserFingerPrint

class DataManager {

    private var id: Int = 2

    var streams = mutableListOf<ItemFingerPrint>(
//        StreamFingerPrint(Stream(0, "#general", listOf(Topic(0, "Testing"), Topic(1, "Bruh")), true)),
//        StreamFingerPrint(Stream(0, "#Development", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")), true)),
//        StreamFingerPrint(Stream(0, "#Design", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
//        StreamFingerPrint(Stream(0, "#PR", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
//        StreamFingerPrint(Stream(0, "test3", listOf(Topic(0, "dsff"), Topic(1, "sdfdsff")))),
    )

    var subscribedStreams: MutableList<ItemFingerPrint> = streams.filter { (it as StreamFingerPrint).stream.isSubscribed} as MutableList<ItemFingerPrint>

    var messages = mutableListOf<ItemFingerPrint>(
        DateDividerFingerPrint(
            Date(
                "17 Окт"
            )
        ),
        MessageFingerPrint(
            Content(
                0, 0, "Данияр", "Привет", mutableListOf(
                    Reaction(1, "\uD83D\uDE09", 2),
                    Reaction(1, "\uD83D\uDE09", 3),
                    Reaction(1, "\uD83D\uDE09", 10),
                )
            )
        ),
        MessageFingerPrint(
            Content(
                1, 1, "Данияр", "Привет", mutableListOf(
                    Reaction(2, "\uD83D\uDE09", 2),
                    Reaction(2, "\uD83D\uDE09", 3),
                    Reaction(2, "\uD83D\uDE09", 10),
                )
            )
        )
    )

    val users = listOf<UserFingerPrint>(
        UserFingerPrint(User(0, "John", "xxx@gmail.com", "fdsgfgfg")),
        UserFingerPrint(User(0, "John", "xxx@gmail.com", "fdsgfgfg")),
        UserFingerPrint(User(0, "John", "xxx@gmail.com", "fdsgfgfg")),
        UserFingerPrint(User(0, "John", "xxx@gmail.com", "fdsgfgfg")),
        UserFingerPrint(User(0, "John", "xxx@gmail.com", "fdsgfgfg")),
        UserFingerPrint(User(0, "John", "xxx@gmail.com", "fdsgfgfg"))
    )

    val profile = Profile("Данияр Байганов", "avatar.png", "working", true)

    fun add(type: Int, position: Int, topics: List<TopicFingerPrint>): List<ItemFingerPrint> {
        if (type == 1) {
            streams = ArrayList(streams)
            streams.addAll(position + 1, topics)
            return streams
        } else {
            subscribedStreams = ArrayList(subscribedStreams)
            subscribedStreams.addAll(position + 1, topics)
            return subscribedStreams
        }
    }

    fun remove(type: Int, topics: List<TopicFingerPrint>): List<ItemFingerPrint> {
        if (type == 1) {
            streams = ArrayList(streams)
            streams.removeAll(topics)
            return streams
        } else {
            subscribedStreams = ArrayList(subscribedStreams)
            subscribedStreams.removeAll(topics)
            return subscribedStreams
        }

    }

    fun addMessage(message: String): List<ItemFingerPrint> {
        messages = ArrayList(messages)
        messages.add(
            MessageFingerPrint(
                Content(
                    id++, com.baiganov.fintech.User.getId(), "Данияр", message, mutableListOf()
                )
            )
        )
        return messages
    }

    fun addEmoji(position: Int, emoji: String): MutableList<ItemFingerPrint> {
        messages = ArrayList(messages)
        for (i in messages.indices) {
            val item = messages[i]
            if (item is MessageFingerPrint && item.content.id == position) {
                val reactions = ArrayList(item.content.reactions.map { it.copy() })
                reactions.add(Reaction(com.baiganov.fintech.User.getId(), emoji, 1))
                val content = item.content.copy(reactions = reactions)
                val message = MessageFingerPrint(content)
                messages[i] = message
            }
        }
        return messages
    }

}