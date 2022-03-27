package com.baiganov.fintech.domain.usecase.channels

import com.baiganov.fintech.domain.repository.ChannelsRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class CreateStreamUseCase @Inject constructor(private val repository: ChannelsRepository) {

    fun execute(name: String, description: String): Completable {
        return repository.createStream(name, description)
    }
}