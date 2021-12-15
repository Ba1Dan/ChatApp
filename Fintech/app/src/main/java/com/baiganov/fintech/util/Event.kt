package com.baiganov.fintech.util

import android.net.Uri
import com.baiganov.fintech.presentation.model.TopicFingerPrint

sealed class Event {

    sealed class EventChat : Event() {

        class LoadFirstMessages(
            val streamTitle: String,

            val streamId: Int
        ) : EventChat()

        class LoadNextMessages(
            val streamTitle: String,

            val anchor: Long
        ) : EventChat()

        class AddReaction(
            val messageId: Int,
            val emojiName: String,
            val streamTitle: String,

        ) : EventChat()

        class DeleteReaction(
            val messageId: Int,
            val emojiName: String,
            val streamTitle: String,

        ) : EventChat()

        class SendMessage(
            val topicTitle: String,
            val streamId: Int,
            val message: String
        ) : EventChat()

        class UploadFile(val uri: Uri, val type: String, val name: String) : EventChat()

        class DeleteMessage(
            val messageId: Int,
            val streamTitle: String,
            val streamId: Int,

        ) : EventChat()
    }

    sealed class EventChannels : EventChat() {

        class CreateStream(
            val streamName: String,
            val streamDescription: String,
        ) : EventChannels()

        class OpenStream(
            val position: Int,
            val topics: List<TopicFingerPrint>,
        ) : EventChannels()

        class CloseStream(
            val position: Int,
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
