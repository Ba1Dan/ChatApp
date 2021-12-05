package com.baiganov.fintech.di

import com.baiganov.fintech.presentation.model.UserToUserFingerPrintMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideUsersMapper(): UserToUserFingerPrintMapper {
        return UserToUserFingerPrintMapper()
    }
}