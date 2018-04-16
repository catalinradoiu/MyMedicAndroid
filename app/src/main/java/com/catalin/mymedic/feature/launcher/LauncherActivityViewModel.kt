package com.catalin.mymedic.feature.launcher

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import com.catalin.mymedic.storage.repository.UsersRepository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/16/2018
 */
class LauncherActivityViewModel(usersRepository: UsersRepository) : ViewModel() {

    val isAuthenticated = ObservableBoolean()
    val currentUser: FirebaseUser? = usersRepository.getCurrentUser()

    private val disposables = CompositeDisposable()

    init {
        disposables.add(
            Observable.timer(SPLASH_SCREEN_DURATION, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isAuthenticated.set(currentUser != null) })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    /**
     * Provider for the [LauncherActivityViewModel]
     */
    internal class LauncherActivityViewModelProvider @Inject constructor(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = (LauncherActivityViewModel(usersRepository) as T)

    }

    companion object {
        private const val SPLASH_SCREEN_DURATION = 3000L
    }
}