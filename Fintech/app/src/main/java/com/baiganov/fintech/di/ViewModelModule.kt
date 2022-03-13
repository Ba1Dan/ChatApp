package com.baiganov.fintech.di

import androidx.lifecycle.ViewModel
import com.baiganov.fintech.presentation.ui.channels.streams.StreamsViewModel
import com.baiganov.fintech.presentation.ui.chat.ChatViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    fun bindChatViewModel(viewModel: ChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StreamsViewModel::class)
    fun bindStreamsViewModel(viewModel: StreamsViewModel): ViewModel
}