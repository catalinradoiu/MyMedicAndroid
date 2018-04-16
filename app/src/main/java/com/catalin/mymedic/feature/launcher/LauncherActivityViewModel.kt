package com.catalin.mymedic.feature.launcher

import android.databinding.ObservableBoolean
import com.catalin.mymedic.storage.repository.UsersRepository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/16/2018
 */
class LauncherActivityViewModel @Inject constructor(usersRepository: UsersRepository) {

    val isAuthenticated = ObservableBoolean()
    val currentUser: FirebaseUser? = usersRepository.getCurrentUser()

    init {
        Observable.timer(SPLASH_SCREEN_DURATION, TimeUnit.MILLISECONDS).subscribeOn(
            Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isAuthenticated.set(currentUser != null) })
    }

    companion object {
        private const val SPLASH_SCREEN_DURATION = 3000L
    }
}