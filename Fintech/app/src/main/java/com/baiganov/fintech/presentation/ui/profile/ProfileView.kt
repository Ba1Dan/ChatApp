package com.baiganov.fintech.presentation.ui.profile

import com.baiganov.fintech.model.response.User
import com.baiganov.fintech.presentation.ui.people.ChatState
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ProfileView : MvpView {

    @AddToEndSingle
    fun render(state: ChatState<User>)
}