package com.baiganov.fintech.domain.usecase.chat

import com.baiganov.fintech.domain.repository.MessageRepository
import io.reactivex.Completable
import javax.inject.Inject

class EditTopicUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    fun execute(messageId: Int, newTopic: String): Completable {
        return messageRepository.editTopic(messageId, newTopic)
    }
}