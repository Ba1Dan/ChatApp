package com.baiganov.fintech.domain.usecase

import com.baiganov.fintech.domain.repository.MessageRepository
import io.reactivex.Completable
import javax.inject.Inject

class EditMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    fun execute(messageId: Int, content: String): Completable {
        return messageRepository.editMessage(messageId, content)
    }
}