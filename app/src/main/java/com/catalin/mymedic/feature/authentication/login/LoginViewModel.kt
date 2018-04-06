package com.catalin.mymedic.feature.authentication.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.catalin.mymedic.feature.authentication.AuthenticationValidator
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/6/2018
 */
class LoginViewModel(private val usersRepository: UsersRepository, private val authenticationValidator: AuthenticationValidator) : ViewModel() {

    val email = ObservableField<String>("")
    val password = ObservableField<String>("")
    val validEmail = ObservableBoolean(true)
    val validPassword = ObservableBoolean(true)
    val loginResult = ObservableField<OperationResult>(OperationResult.NoOperation)

    private val disposables = CompositeDisposable()

    fun loginUser() {
        val isValidEmail = authenticationValidator.isValidEmailAdress(email.get())
        val isValidPassword = authenticationValidator.isValidPassword(password.get())
        validEmail.set(isValidEmail)
        validPassword.set(isValidPassword)
        if (isValidEmail && isValidPassword) {
            disposables.add(usersRepository.getUserByEmailAndPassword(email.get(), password.get()).mainThreadSubscribe(
                Consumer { result ->
                    if (result.user.isEmailVerified) {
                        loginResult.set(OperationResult.Success())
                    } else {
                        loginResult.set(OperationResult.Error(EMAIL_NOT_VERIFIED))
                    }
                },
                Consumer { error ->
                    loginResult.set(OperationResult.Error(error.localizedMessage))
                }
            ))
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    internal class LoginViewModelProvider @Inject constructor(
        private val usersRepository: UsersRepository,
        private val authenticationValidator: AuthenticationValidator
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = (LoginViewModel(usersRepository, authenticationValidator) as T)
    }

    companion object {
        const val EMAIL_NOT_VERIFIED = "Email not verified"
    }
}