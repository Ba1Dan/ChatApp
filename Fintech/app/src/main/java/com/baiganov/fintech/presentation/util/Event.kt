package com.baiganov.fintech.presentation.util

import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint

sealed class Event {

    sealed class EventChat : Event() {

        data class LoadFirstMessages(
            val streamTitle: String,
            val topicTitle: String,
            val streamId: Int
        ) : EventChat()

        data class LoadNextMessages(
            val streamTitle: String,
            val topicTitle: String,
            val anchor: Long
        ) : EventChat()

        data class AddReaction(
            val messageId: Int,
            val emojiName: String,
            val streamTitle: String,
            val topicTitle: String
        ) : EventChat()

        data class DeleteReaction(
            val messageId: Int,
            val emojiName: String,
            val streamTitle: String,
            val topicTitle: String
        ) : EventChat()

        data class SendMessage(
            val streamTitle: String,
            val streamId: Int,
            val topicTitle: String,
            val message: String
        ) : EventChat()

        data class UploadFile(val temp: String) : EventChat()

        data class DeleteMessage(
            val messageId: Int,
            val streamTitle: String,
            val topicTitle: String
        ) : EventChat()
    }

    sealed class EventChannels : EventChat() {

        data class LoadStreams(
            val streamTitle: String,
            val topicTitle: String,
            val streamId: Int
        ) : EventChannels()

        data class OpenStream(
            val type: Int,
            val position: Int,
            val topics: List<TopicFingerPrint>,
        ) : EventChannels()

        data class CloseStream(
            val type: Int,
            val topics: List<TopicFingerPrint>
        ) : EventChannels()

        data class SearchStreams(
            val searchQuery: String,
            val type: Int
        ) : EventChannels()

    }

    sealed class EventPeople : EventChat() {

        object LoadUsers : EventPeople()
    }

    sealed class EventProfile : EventChat() {

        object LoadProfile : EventProfile()
    }
}
