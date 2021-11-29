package com.baiganov.fintech.presentation.ui.people

import com.baiganov.fintech.presentation.ui.people.adapters.UserFingerPrint
import com.baiganov.fintech.util.State
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface PeopleView : MvpView {

    @AddToEndSingle
    fun render(state: State<List<UserFingerPrint>>)
}