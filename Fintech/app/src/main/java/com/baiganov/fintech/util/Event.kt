package com.baiganov.fintech.util

import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint

sealed class Event {

    sealed class EventChat : Event() {

        class LoadFirstMessages(
            val streamTitle: String,
            val topicTitle: String,
            val streamId: Int
        ) : EventChat()

        class LoadNextMessages(
            val streamTitle: String,
            val topicTitle: String,
            val anchor: Long
        ) : EventChat()

        class AddReaction(
            val messageId: Int,
            val emojiName: String,
            val streamTitle: String,
            val topicTitle: String
        ) : EventChat()

        class DeleteReaction(
            val messageId: Int,
            val emojiName: String,
            val streamTitle: String,
            val topicTitle: String
        ) : EventChat()

        class SendMessage(
            val streamTitle: String,
            val streamId: Int,
            val topicTitle: String,
            val message: String
        ) : EventChat()

        class UploadFile(val temp: String) : EventChat()

        class DeleteMessage(
            val messageId: Int,
            val streamTitle: String,
            val topicTitle: String
        ) : EventChat()
    }

    sealed class EventChannels : EventChat() {

        class LoadStreams(
            val streamTitle: String,
            val topicTitle: String,
            val streamId: Int
        ) : EventChannels()

        class OpenStream(
            val position: Int,
            val topics: List<TopicFingerPrint>,
        ) : EventChannels()

        class CloseStream(
            val topics: List<TopicFingerPrint>
        ) : EventChannels()

        class SearchStreams(
            val searchQuery: String,
        ) : EventChannels()

    }

    sealed class EventPeople : EventChat() {

        object LoadUsers : EventPeople()
    }

    sealed class EventProfile : EventChat() {

        object LoadProfile : EventProfile()
    }
}
