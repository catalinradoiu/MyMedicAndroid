package com.catalin.mymedic.feature.authentication.passwordreset

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.catalin.mymedic.feature.authentication.AuthenticationValidator
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
class PasswordResetViewModel(
    private val usersRepository: UsersRepository,
    private val authenticationValidator: AuthenticationValidator
) : ViewModel() {

    val email = ObservableField<String>("")
    val validEmail = ObservableBoolean(true)
    val passwordResetResult = ObservableField<OperationResult>(OperationResult.NoOperation)

    private var disposables = CompositeDisposable()

    fun sendPasswordResetEmail() {
        if (authenticationValidator.isValidEmailAdress(email.get())) {
            validEmail.set(true)
            disposables.add(
                usersRepository.sendPasswordResetEmail(email.get()).mainThreadSubscribe(Action {
                    passwordResetResult.set(OperationResult.Success())
                }, Consumer {
                    passwordResetResult.set(OperationResult.Error(it.message))
                })
            )
        } else {
            validEmail.set(false)
        }
    }

    fun clearFields() {
        email.set("")
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class PasswordResetViewModelProvider @Inject constructor(
        private val usersRepository: UsersRepository,
        private val authenticationValidator: AuthenticationValidator
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = (PasswordResetViewModel(usersRepository, authenticationValidator) as T)
    }
}