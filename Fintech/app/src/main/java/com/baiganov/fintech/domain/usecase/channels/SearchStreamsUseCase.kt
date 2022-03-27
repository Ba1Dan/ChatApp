package com.baiganov.fintech.domain.usecase.channels

import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.presentation.model.StreamFingerPrint
import com.baiganov.fintech.presentation.ui.channels.ChannelsPages
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class SearchStreamsUseCase @Inject constructor(private val repository: ChannelsRepository) {

    fun execute(searchQuery: String, type: Int): Flowable<List<StreamFingerPrint>> {
        return when (type) {
            ChannelsPages.SUBSCRIBED.ordinal -> repository.searchSubscribedStreams(searchQuery)
                .map {
                    mapToFingerPrint(it)
                }
            ChannelsPages.ALL_STREAMS.ordinal -> repository.searchAllStreams(searchQuery)
                .map {
                    mapToFingerPrint(it)
                }
            else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $type")
        }
    }

    private fun mapToFingerPrint(list: List<StreamEntity>): List<StreamFingerPrint> {
        return list.map { stream ->
            StreamFingerPrint(
                stream
            )
        }
    }
}