package com.baiganov.fintech.ui

import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

sealed class MainScreenState {

    class Result(val items: List<ItemFingerPrint>) : MainScreenState()

    object Loading : MainScreenState()

    class Error(val error: Throwable) : MainScreenState()
}