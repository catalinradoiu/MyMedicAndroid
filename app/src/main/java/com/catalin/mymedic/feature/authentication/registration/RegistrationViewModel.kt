package com.catalin.mymedic.feature.authentication.registration

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.data.User
import com.catalin.mymedic.feature.authentication.AuthenticationValidator
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.Constants
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * ViewModel for the RegistrationActivity
 *
 * @author catalinradoiu
 * @since 2/12/2018
 */
internal class RegistrationViewModel(private val usersRepository: UsersRepository, private val authenticationValidator: AuthenticationValidator) : ViewModel() {

    val email: ObservableField<String> = ObservableField("")
    val password: ObservableField<String> = ObservableField("")
    val passwordConfirmation: ObservableField<String> = ObservableField("")
    val validEmail = ObservableBoolean(true)
    val validPassword = ObservableBoolean(true)
    val passwordsMatch = ObservableBoolean(true)
    val registrationResult = ObservableField<OperationResult>(OperationResult.NoOperation)

    private val disposables = CompositeDisposable()


    /**
     * Validates the user input and registers the user with the data from the ObservableFields if the input is valid
     * Sets the corresponding result of the operation depending on its status ( success, failure )
     */
    fun registerUser() {
        val isValidEmail = authenticationValidator.isValidEmailAdress(email.get().orEmpty())
        val isValidPassword = authenticationValidator.isValidPassword(password.get().orEmpty())
        validEmail.set(isValidEmail)
        validPassword.set(isValidPassword)
        passwordsMatch.set(true)
        if (isValidEmail && isValidPassword) {
            if (password.get() == passwordConfirmation.get()) {
                passwordsMatch.set(true)
                disposables.add(
                    usersRepository.registerUser(
                        User("", email.get().orEmpty(), 0, Gender.NOT_COMPLETED, Constants.PATIENT, FirebaseDatabaseConfig.DEFAULT_USER_IMAGE_LOCATION),
                        password.get().orEmpty()
                    ).mainThreadSubscribe(Action {
                        registrationResult.set(OperationResult.Success())
                    }, Consumer {
                        registrationResult.set(OperationResult.Error(it.localizedMessage))
                    })
                )
            }
        } else {
            passwordsMatch.set(false)
        }
    }

    fun clearFields() {
        email.set("")
        password.set("")
        passwordConfirmation.set("")
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    /**
     * Provider class for the RegistrationViewModel
     */
    internal class RegistrationViewModelProvider @Inject constructor(
        private val usersRepository: UsersRepository,
        private val authenticationValidator: AuthenticationValidator
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = (RegistrationViewModel(usersRepository, authenticationValidator) as T)
    }
}