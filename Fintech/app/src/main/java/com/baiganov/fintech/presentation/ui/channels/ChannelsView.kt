package com.baiganov.fintech.presentation.ui.channels

import com.baiganov.fintech.util.State
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ChannelsView : MvpView {

    @AddToEndSingle
    fun render(state: State<String>)
}