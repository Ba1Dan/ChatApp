package com.baiganov.fintech.presentation.ui.chat

import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.util.State
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ChatView : MvpView {

    @AddToEndSingle
    fun render(state: State<List<ItemFingerPrint>>)
}