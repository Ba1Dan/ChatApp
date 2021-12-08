package com.baiganov.fintech.presentation.ui.channels.streams

import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.util.State
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface StreamsView : MvpView {

    @AddToEndSingle
    fun render(state: State<List<ItemFingerPrint>>)
}