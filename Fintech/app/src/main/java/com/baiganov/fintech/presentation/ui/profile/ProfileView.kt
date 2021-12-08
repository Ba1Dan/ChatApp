package com.baiganov.fintech.presentation.ui.profile

import com.baiganov.fintech.data.model.response.User
import com.baiganov.fintech.util.State
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ProfileView : MvpView {

    @AddToEndSingle
    fun render(state: State<User>)
}