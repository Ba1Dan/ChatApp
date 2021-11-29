package com.baiganov.fintech.presentation.ui.people

sealed class ChatState<T> {

    class Result<T>(val data: T) : ChatState<T>()
    class Error<T>(val message: String?) : ChatState<T>()
    class Loading<T> : ChatState<T>()
}