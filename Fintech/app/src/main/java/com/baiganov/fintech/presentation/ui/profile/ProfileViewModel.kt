package com.baiganov.fintech.presentation.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.model.User
import com.baiganov.fintech.domain.repository.ProfileRepository
import com.baiganov.fintech.presentation.NetworkManager
import com.baiganov.fintech.presentation.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val networkManager: NetworkManager
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _state: MutableLiveData<State<User>> = MutableLiveData()
    val state: LiveData<State<User>>
        get() = _state

    init {
        loadProfile()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun loadProfile() {
        if (networkManager.isConnected().value) {

            repository.loadProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    _state.value = State.Loading
                }
                .subscribeBy(
                    onSuccess = { _state.value = State.Result(it) },
                    onError = {
                        _state.value = State.Error(it.message)
                    }
                )
                .addTo(compositeDisposable)
        }
    }
}