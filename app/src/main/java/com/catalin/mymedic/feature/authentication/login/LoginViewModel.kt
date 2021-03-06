package com.catalin.mymedic.feature.authentication.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.catalin.mymedic.feature.authentication.AuthenticationValidator
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/6/2018
 */
//TODO : The preferences manager can be moved to the users repository in this case
class LoginViewModel(
    private val usersRepository: UsersRepository,
    private val authenticationValidator: AuthenticationValidator
) : ViewModel() {

    val email = ObservableField<String>("")
    val password = ObservableField<String>("")
    val validEmail = ObservableBoolean(true)
    val validPassword = ObservableBoolean(true)
    val loginResult = ObservableField<OperationResult>(OperationResult.NoOperation)

    val isLoading = ObservableBoolean(false)

    private val disposables = CompositeDisposable()

    fun loginUser() {
        val isValidEmail = authenticationValidator.isValidEmailAdress(email.get().orEmpty())
        val isValidPassword = authenticationValidator.isValidPassword(password.get().orEmpty())
        validEmail.set(isValidEmail)
        validPassword.set(isValidPassword)
        if (isValidEmail && isValidPassword) {

            isLoading.set(true)
            disposables.add(
                usersRepository.getUserByEmailAndPassword(email.get().orEmpty(), password.get().orEmpty())
                    .flatMap { authResult ->
                        usersRepository.getUserById(authResult.user.uid, false).map { Pair(authResult, it) }
                    }
                    .mainThreadSubscribe(
                        Consumer { (authResult, user) ->
                            isLoading.set(false)
                            if (authResult.user.isEmailVerified) {
                                usersRepository.updateUserLocalData(user, authResult.user.uid)
                                usersRepository.updateUserNotificationToken(FirebaseInstanceId.getInstance().token.orEmpty())
                                loginResult.set(OperationResult.Success())
                            } else {
                                loginResult.set(OperationResult.Error(EMAIL_NOT_VERIFIED))
                            }
                        },
                        Consumer { error ->
                            isLoading.set(false)
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