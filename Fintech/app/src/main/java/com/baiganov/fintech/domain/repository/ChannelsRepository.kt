package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.Completable
import io.reactivex.Flowable

interface ChannelsRepository {

    fun getAllStreams(): Completable

    fun getSubscribedStreams(): Completable

    fun searchStreams(searchQuery: String, type: Int?): Flowable<List<StreamEntity>>

    fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>>
}