package com.baiganov.fintech.domain.usecase.channels

import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.presentation.ui.channels.ChannelsPages
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class GetStreamsUseCase @Inject constructor(private val repository: ChannelsRepository) {

    fun execute(type: Int): Completable {
        return when (type) {
            ChannelsPages.SUBSCRIBED.ordinal -> repository.getSubscribedStreams()
            ChannelsPages.ALL_STREAMS.ordinal -> repository.getAllStreams()
            else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $type")
        }
    }
}