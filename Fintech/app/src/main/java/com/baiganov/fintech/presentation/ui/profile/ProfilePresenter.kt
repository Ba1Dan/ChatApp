package com.baiganov.fintech.presentation.ui.profile

import com.baiganov.fintech.domain.repositories.ProfileRepository
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
class ProfilePresenter @Inject constructor(private val repository: ProfileRepository) :
    MvpPresenter<ProfileView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun loadProfile() {
        viewState.render(State.Loading())
        repository.loadProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {  viewState.render(State.Result(it)) },
                onError = {
                    viewState.render(State.Error(it.message))
                }
            )
            .addTo(compositeDisposable)
    }
}