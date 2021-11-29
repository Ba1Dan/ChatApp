package com.baiganov.fintech.util


sealed class State<T> {

    class Result<T>(val data: T) : State<T>()
    class Error<T>(val message: String?) : State<T>()
    class Loading<T>() : State<T>()
}