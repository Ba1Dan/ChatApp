package com.baiganov.fintech.util


sealed class State<out T> {

    class Result<T>(val data: T) : State<T>()
    class Error(val message: String?) : State<Nothing>()
    object Loading : State<Nothing>()
    object LoadingFile : State<Nothing>()
    class AddFile(val uri: String) : State<Nothing>()
}