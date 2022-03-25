package com.baiganov.fintech.domain.usecase

import com.baiganov.fintech.domain.repository.MessageRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    fun execute(messageId: Int): Completable {
        return messageRepository.deleteMessage(messageId)
    }
}