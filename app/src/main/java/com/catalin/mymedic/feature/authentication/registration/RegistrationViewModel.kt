package com.catalin.mymedic.feature.authentication.registration

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.data.User
import com.catalin.mymedic.storage.repository.UsersRepository
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
internal class RegistrationViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    val email: ObservableField<String> = ObservableField("")
    val password: ObservableField<String> = ObservableField("")
    val passwordConfirmation: ObservableField<String> = ObservableField("")
    val firstName: ObservableField<String> = ObservableField("")
    val lastName: ObservableField<String> = ObservableField("")
    val birthDate: ObservableLong = ObservableLong(0)
    val validEmail = ObservableBoolean(true)

    val validPassword = ObservableBoolean(true)
    val passwordsMatch = ObservableBoolean(true)
    val validName = ObservableBoolean(true)
    val registrationResult = ObservableField<OperationResult>(OperationResult.NoOperation)

    private val disposables = CompositeDisposable()


    /**
     * Registers the user with the data from the ObservableFields
     * Sets the corresponding result of the operation depending on its status ( success, failure )
     */
    fun registerUser() {
        usersRepository.registerUser(User(firstName.get(), lastName.get(), email.get(), 0, Gender.MALE), password.get())
            .mainThreadSubscribe(Action {
                registrationResult.set(OperationResult.Success())
            }, Consumer {
                registrationResult.set(OperationResult.Error(it.localizedMessage))
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    /**
     * Provider class for the RegistrationViewModel
     */
    class RegistrationViewModelProvider @Inject constructor(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = (RegistrationViewModel(
            usersRepository
        ) as T)
    }

    sealed class OperationResult {
        data class Error(val message: String) : OperationResult()
        data class Success(val message: String = "") : OperationResult()
        object NoOperation : OperationResult()
    }
}