package com.baiganov.fintech.domain.usecase.chat

import android.net.Uri
import com.baiganov.fintech.data.model.FileResponse
import com.baiganov.fintech.domain.repository.MessageRepository
import io.reactivex.Single
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    fun execute(uri: Uri, type: String, name: String): Single<FileResponse> {
        return messageRepository.uploadFile(uri, type, name)
    }
}