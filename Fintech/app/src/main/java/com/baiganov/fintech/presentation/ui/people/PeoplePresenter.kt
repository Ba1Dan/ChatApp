package com.baiganov.fintech.presentation.ui.people

import com.baiganov.fintech.domain.repository.PeopleRepository
import com.baiganov.fintech.presentation.NetworkManager
import com.baiganov.fintech.presentation.model.UserToUserFingerPrintMapper
import com.baiganov.fintech.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class PeoplePresenter @Inject constructor(
    private val repository: PeopleRepository,
    private val userToUserFingerPrintMapper: UserToUserFingerPrintMapper,
    private val networkManager: NetworkManager
) :
    MvpPresenter<PeopleView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun getUsers() {
        if (networkManager.isConnected().value) {
            viewState.render(State.Loading())
            repository.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(userToUserFingerPrintMapper)
                .subscribeBy(
                    onSuccess = { users ->
                        viewState.render(State.Result(users))
                    },
                    onError = { viewState.render(State.Error(it.message)) }
                )
                .addTo(compositeDisposable)
        }

    }
}