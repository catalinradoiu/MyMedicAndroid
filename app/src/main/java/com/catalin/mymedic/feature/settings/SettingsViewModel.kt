package com.catalin.mymedic.feature.settings

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.repository.UsersRepository
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/25/2018
 */
class SettingsViewModel(private val usersRepository: UsersRepository, private val preferencesManager: SharedPreferencesManager) : ViewModel() {

    fun logOutUser() {
        usersRepository.logOutUser()
    }

    fun isMessageNotificationEnabled() = preferencesManager.messageNotificationsEnabled

    fun isAppointmentNotificationEnabled() = preferencesManager.appointmentNotificationsEnabled

    fun setMessageNotificationState(enabled: Boolean) {
        preferencesManager.messageNotificationsEnabled = enabled
    }

    fun setAppointmentNotificationState(enabled: Boolean) {
        preferencesManager.appointmentNotificationsEnabled = enabled
    }

    class Factory @Inject constructor(private val usersRepository: UsersRepository, private val preferencesManager: SharedPreferencesManager) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = SettingsViewModel(usersRepository, preferencesManager) as T

    }
}