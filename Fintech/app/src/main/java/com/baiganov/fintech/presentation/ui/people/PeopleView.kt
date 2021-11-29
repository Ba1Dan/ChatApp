package com.baiganov.fintech.presentation.ui.people

import com.baiganov.fintech.presentation.ui.people.adapters.UserFingerPrint
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface PeopleView : MvpView {

    @AddToEndSingle
    fun render(state: ChatState<List<UserFingerPrint>>)
}