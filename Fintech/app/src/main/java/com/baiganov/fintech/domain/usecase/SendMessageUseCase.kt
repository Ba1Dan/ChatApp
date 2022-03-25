package com.baiganov.fintech.domain.usecase

import com.baiganov.fintech.domain.repository.MessageRepository
import com.baiganov.fintech.presentation.ui.chat.ChatViewModel
import com.baiganov.fintech.presentation.util.State
import io.reactivex.Completable
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