package com.baiganov.fintech.di

import android.content.Context
import com.baiganov.fintech.data.UriReader
import com.baiganov.fintech.data.UriReaderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UriReaderModule {

    @Singleton
    @Provides
    fun provideUriReader(context: Context): UriReader {
        return UriReaderImpl(context)
    }
}