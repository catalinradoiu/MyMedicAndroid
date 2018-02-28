package com.catalin.mymedic.feature.authentication

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
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
    val registrationResult = ObservableBoolean(true)

    private val disposables = CompositeDisposable()


    fun registerUser() {
        usersRepository.registerUser(email.get(), password.get(), firstName.get(), lastName.get(), 0, Gender.MALE)
                .mainThreadSubscribe(Action {
                    registrationResult.set(true)
                }, Consumer {
                    registrationResult.set(false)
                })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class RegistrationViewModelProvider @Inject constructor(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = (RegistrationViewModel(usersRepository) as T)

    }

    enum class RegistrationResult {
        NO_INTERNET, EMAIL_IN_USE, NONE
    }
}