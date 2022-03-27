package com.baiganov.fintech.domain.usecase.chat

import com.baiganov.fintech.domain.repository.MessageRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {

    fun execute(
        streamId: Int,
        message: String,
        topicTitle: String
    ): Completable {
        return messageRepository.sendMessage(streamId, message, topicTitle)
    }
}