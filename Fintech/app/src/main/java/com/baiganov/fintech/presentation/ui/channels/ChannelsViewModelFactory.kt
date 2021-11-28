package com.baiganov.fintech.presentation.ui.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.domain.repositories.ChannelsRepository

class ChannelsViewModelFactory(private val channelsRepository: ChannelsRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChannelsViewModel::class.java)) {
            return ChannelsViewModel(channelsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}