package com.baiganov.fintech.domain.usecase.chat

import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.domain.repository.MessageRepository
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.presentation.ui.chat.recyclerview.DateDividerFingerPrint
import com.baiganov.fintech.presentation.util.formatDateByDay
import io.reactivex.Flowable
import javax.inject.Inject

class GetMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    fun execute(topicTitle: String?, streamId: Int): Flowable<List<ItemFingerPrint>> {
        return if (topicTitle != null) {
            messageRepository.getTopicMessages(topicTitle, streamId).map {  streamEntities ->
                streamEntities.groupBy { formatDateByDay(it.timestamp) }
                    .flatMap { (date: String, messagesByDate: List<MessageEntity>) ->
                        listOf(DateDividerFingerPrint(date)) +
                                messagesByDate.map { message -> MessageFingerPrint(message) }
                    }
            }
        } else {
            messageRepository.getStreamMessages(streamId).map { messages ->
                getMessagesByDivider(topicTitle, messages)
            }
        }
    }

    //if open stream then show date else show date and topicName
    private fun getMessagesByDivider(topicTitle: String?, messages: List<MessageEntity>): List<ItemFingerPrint> {
        return topicTitle?.let {
            messages.groupBy { formatDateByDay(it.timestamp) }
                .flatMap { (date: String, messagesByDate: List<MessageEntity>) ->
                    listOf(DateDividerFingerPrint(date)) +
                            messagesByDate.map { message -> MessageFingerPrint(message) }
                }
        } ?: messages.groupBy { formatDateByDay(it.timestamp) }
            .flatMap { (date: String, messagesByDate: List<MessageEntity>) ->
                listOf(DateDividerFingerPrint(date)) +
                        getMessagesByDateAndTopicName(messagesByDate)
            }
    }

    private fun getMessagesByDateAndTopicName(messagesByDate: List<MessageEntity>): List<ItemFingerPrint> {
        return messagesByDate.flatMapIndexed { i: Int, message: MessageEntity ->
            if (i == 0 || messagesByDate[i - 1].topicName != message.topicName) {
                listOf(
                    DateDividerFingerPrint(message.topicName),
                    MessageFingerPrint(message)
                )
            } else {
                listOf(MessageFingerPrint(message))
            }
        }
    }
}