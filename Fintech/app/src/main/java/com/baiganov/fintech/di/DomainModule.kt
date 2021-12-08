package com.baiganov.fintech.di

import com.baiganov.fintech.data.repository.ChannelsRepositoryImpl
import com.baiganov.fintech.data.repository.MessageRepositoryImpl
import com.baiganov.fintech.data.repository.PeopleRepositoryImpl
import com.baiganov.fintech.data.repository.ProfileRepositoryImpl
import com.baiganov.fintech.domain.repository.ChannelsRepository
import com.baiganov.fintech.domain.repository.MessageRepository
import com.baiganov.fintech.domain.repository.PeopleRepository
import com.baiganov.fintech.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    fun bindPeopleRepository(impl: PeopleRepositoryImpl): PeopleRepository

    @Binds
    fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    fun bindChannelsRepository(impl: ChannelsRepositoryImpl): ChannelsRepository
}