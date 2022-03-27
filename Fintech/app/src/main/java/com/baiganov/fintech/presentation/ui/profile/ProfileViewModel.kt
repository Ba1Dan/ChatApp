package com.baiganov.fintech.presentation.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.model.User
import com.baiganov.fintech.domain.usecase.profile.LoadProfileUseCase
import com.baiganov.fintech.presentation.util.NetworkManager
import com.baiganov.fintech.presentation.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val networkManager: NetworkManager,
    private val loadProfileUseCase: LoadProfileUseCase
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
            loadProfileUseCase.execute()
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