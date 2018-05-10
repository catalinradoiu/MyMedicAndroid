package com.catalin.mymedic.feature.authentication.passwordreset

import android.databinding.ObservableField
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/10/2018
 */
class PasswordResetViewModel @Inject constructor(private val usersRepository: UsersRepository) {

    val email = ObservableField<String>("")
    val passwordResetResult = ObservableField<OperationResult>(OperationResult.NoOperation)

    private var disposables = CompositeDisposable()

    fun sendPasswordResetEmail() {
        disposables.add(
            usersRepository.sendPasswordResetEmail(email.get()).mainThreadSubscribe(Action {
                passwordResetResult.set(OperationResult.Success())
            }, Consumer {
                passwordResetResult.set(OperationResult.Error(it.message))
            })
        )
    }
}