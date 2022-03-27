package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.Completable
import io.reactivex.Flowable

interface ChannelsRepository {

    fun getSubscribedStreams(): Completable

    fun getAllStreams(): Completable

    fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>>

    fun searchAllStreams(searchQuery: String): Flowable<List<StreamEntity>>

    fun createStream(name: String, description: String): Completable
}